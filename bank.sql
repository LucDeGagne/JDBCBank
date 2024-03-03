CREATE OR REPLACE PACKAGE bank IS
 PROCEDURE get_branch (in_add IN VARCHAR2, out_Bno OUT VARCHAR2);
 PROCEDURE get_account (in_name IN VARCHAR2, in_Bno IN VARCHAR2, out_Ano OUT VARCHAR2);
 PROCEDURE open_branch (in_add IN VARCHAR2, out_Bno OUT VARCHAR2);
 PROCEDURE close_branch (in_Bno IN VARCHAR2, out_chk OUT NUMBER);
 PROCEDURE create_customer (in_name IN VARCHAR2, out_Cno OUT VARCHAR2);
 PROCEDURE remove_customer (in_name IN VARCHAR2, out_chk OUT NUMBER);
 PROCEDURE open_account (in_cust IN VARCHAR2, in_Bno IN VARCHAR2, in_amount IN INTEGER, out_chk OUT INTEGER);
 PROCEDURE close_account (in_Ano IN VARCHAR2, out_chk OUT INTEGER);
 PROCEDURE withdraw (in_Ano IN VARCHAR2, in_amount IN NUMBER, out_chk OUT INTEGER);
 PROCEDURE deposit (in_Ano IN VARCHAR2, in_amount IN NUMBER);
 PROCEDURE show_branch (in_Bno IN VARCHAR2, out_add OUT VARCHAR2, out_cursor OUT SYS_REFCURSOR, out_total OUT NUMBER);
 PROCEDURE show_all_branches (out_cursor OUT SYS_REFCURSOR);
 PROCEDURE show_customer (in_name IN VARCHAR2, out_chk OUT INTEGER, out_Cno OUT VARCHAR2, out_cursor OUT SYS_REFCURSOR, out_total OUT NUMBER);
END bank;
/


CREATE OR REPLACE PACKAGE BODY bank IS
 PROCEDURE get_branch (in_add IN VARCHAR2, out_Bno OUT VARCHAR2) IS
  v_Bno VARCHAR2(5);
 BEGIN
  SELECT B# INTO v_Bno FROM Branch WHERE Address = in_add OR B# = in_add;
  out_Bno := v_Bno;
  EXCEPTION WHEN NO_DATA_FOUND THEN
   out_Bno := NULL;
 END get_branch;


 PROCEDURE get_account (in_name IN VARCHAR2, in_Bno IN VARCHAR2, out_Ano OUT VARCHAR2) IS
  v_Ano VARCHAR2(10);
 BEGIN
  SELECT a.A# INTO v_Ano FROM Account a, Customer c WHERE a.A# LIKE CONCAT(in_Bno, '%') AND a.C# = c.C# AND Name = in_name;
  out_Ano := v_Ano;
  EXCEPTION WHEN NO_DATA_FOUND THEN
   out_Ano := NULL;
 END get_account;


 PROCEDURE open_branch (in_add IN VARCHAR2, out_Bno OUT VARCHAR2) IS 
  num NUMBER := 0;
  v_count NUMBER;
  v_Bno VARCHAR2(5);
  cursor c1 is SELECT * FROM Branch ORDER BY B#;
 BEGIN
  SELECT COUNT(*) INTO v_count FROM Branch WHERE Address = in_add;
  IF v_count > 0 THEN 
   out_Bno := NULL;
   RETURN;
  END IF;
  FOR item IN c1
  LOOP
   EXIT WHEN TO_NUMBER(item.B#, '000') != num;
   DBMS_OUTPUT.PUT_LINE(TO_NUMBER(item.B#, '000'));
   DBMS_OUTPUT.PUT_LINE(item.Address);
   num := num + 1;
  END LOOP;
  DBMS_OUTPUT.PUT_LINE(num);
  v_Bno := lpad(TO_CHAR(num),3, '0');
  INSERT INTO Branch VALUES(v_Bno, in_add);
  out_Bno := v_Bno;
END open_branch;



 PROCEDURE close_branch (in_Bno IN VARCHAR2, out_chk OUT NUMBER) IS
  v_count NUMBER;
  v_Ano VARCHAR2(5);
 BEGIN
  v_Ano := CONCAT(in_Bno, '%');
  SELECT COUNT(*) INTO v_count FROM Account WHERE A# LIKE v_Ano;
  IF v_count > 0 THEN
   out_chk := 0;
   RETURN;
  END IF;
  DELETE FROM Branch WHERE B# = in_Bno;
  out_chk := 1;
 END close_branch;



 PROCEDURE create_customer (in_name IN VARCHAR2, out_Cno OUT VARCHAR2) IS
  num NUMBER := 0;
  v_Cno VARCHAR2(5);
  v_count NUMBER;
  v_max VARCHAR2(5);
 BEGIN
  SELECT COUNT(*) INTO v_count FROM Customer WHERE Name = in_name;
  IF v_count > 0 THEN
   out_Cno := NULL;
   RETURN;
  END IF;
  SELECT COUNT(*) INTO v_count FROM Customer ;
  IF v_count < 1 THEN
   out_Cno := '00000';
   INSERT INTO Customer VALUES(out_Cno, in_name);
   RETURN;
  END IF;
  SELECT MAX(C#) into v_max FROM Customer;
  v_Cno := lpad(TO_CHAR(TO_NUMBER(v_max, '00000') + 1),5, '0');
  INSERT INTO Customer VALUES(v_Cno, in_name);
  out_Cno := v_Cno;
 END create_customer;



 PROCEDURE remove_customer (in_name IN VARCHAR2, out_chk OUT NUMBER) IS
  v_count_C NUMBER;
  v_count_A NUMBER;
  v_Cno VARCHAR2(5);
 BEGIN
  SELECT COUNT(*) INTO v_count_C FROM Customer WHERE Name = in_name;
  IF v_count_C < 1 THEN
   out_chk := 0;
   DBMS_OUTPUT.PUT_LINE('No customer exists with this name');
   RETURN;
  END IF;
  SELECT C# INTO v_Cno FROM Customer WHERE Name = in_name;
  DBMS_OUTPUT.PUT_LINE(in_name);
  DBMS_OUTPUT.PUT_LINE(v_Cno);
  SELECT COUNT(*) INTO v_count_A FROM Account WHERE C# = v_Cno;
  IF v_count_A > 0 THEN
   DBMS_OUTPUT.PUT_LINE('Account exists for this customer');
   out_chk := 1;
   RETURN;
  END IF;
  DELETE FROM Customer WHERE C# = v_Cno;
  out_chk := 2;
 END remove_customer;



 PROCEDURE open_account (in_cust IN VARCHAR2, in_Bno IN VARCHAR2, in_amount IN INTEGER, out_chk OUT INTEGER) IS
  num NUMBER;
  v_count_C NUMBER;
  v_count_A NUMBER;
  v_Cno VARCHAR2(5);
  v_Ano VARCHAR2(10); 
  cursor c1 is SELECT * FROM Account WHERE A# LIKE CONCAT(in_Bno, '%') ORDER BY A#;
 BEGIN
  SELECT COUNT(*) INTO v_count_C FROM Customer WHERE Name = in_cust;
  IF v_count_C < 1 THEN
   out_chk := 0;
   DBMS_OUTPUT.PUT_LINE('No customer exists with this name');
   RETURN;
  END IF;
  SELECT C# INTO v_Cno FROM Customer WHERE Name = in_cust;
  DBMS_OUTPUT.PUT_LINE(v_Cno);
  v_Ano := CONCAT(in_Bno, '%');
  SELECT COUNT(*) INTO v_count_A FROM Account WHERE C# = v_Cno AND A# LIKE v_Ano;
  IF v_count_A > 0 THEN
   out_chk := 1;
   DBMS_OUTPUT.PUT_LINE('Account at this branch and for this customer already exists.');
   RETURN;
  END IF;
  num := 0;
  FOR item IN c1
  LOOP 
   EXIT WHEN TO_NUMBER(SUBSTR(item.A#, 4,4), '0000') != num;
   DBMS_OUTPUT.PUT_LINE(item.A#);
   DBMS_OUTPUT.PUT_LINE(TO_NUMBER(SUBSTR(item.A#, 4,4), '0000'));
   DBMS_OUTPUT.PUT_LINE(item.C#);
   num := num + 1;
  END LOOP;
  DBMS_OUTPUT.PUT_LINE(num);
  v_Ano := CONCAT(in_Bno, lpad(TO_CHAR(num),4,'0'));
  DBMS_OUTPUT.PUT_LINE(v_Ano);
  INSERT INTO Account VALUES(v_Ano, v_Cno, in_amount);
  out_chk := 2;
 END open_account;

 PROCEDURE close_account (in_Ano IN VARCHAR2, out_chk OUT INTEGER) IS
  v_count NUMBER;
 BEGIN
  SELECT COUNT(*) INTO v_count FROM Account WHERE A# = in_Ano;
  IF v_count < 1 THEN
   out_chk := 0;
   DBMS_OUTPUT.PUT_LINE('Account does not exist');
   RETURN;
  END IF;
  SELECT COUNT(*) INTO v_count FROM Account WHERE A# = in_Ano AND Balance = 0;
  IF v_count < 1 THEN
   out_chk := 1;
   DBMS_OUTPUT.PUT_LINE('Account balance not 0');
   RETURN;
  END IF;
  DELETE FROM Account WHERE A# = in_Ano;
  out_chk := 2;
 END close_account;


 PROCEDURE withdraw (in_Ano IN VARCHAR2, in_amount IN NUMBER, out_chk OUT INTEGER) IS
  v_count NUMBER;
 BEGIN
  SELECT COUNT(*) INTO v_count FROM Account WHERE A# = in_Ano AND Balance >= in_amount;
  IF v_count < 1 THEN
   out_chk := 0;
   DBMS_OUTPUT.PUT_LINE('Account does not have enough money.');
   RETURN;
  END IF;
  UPDATE Account SET Balance = ((SELECT Balance FROM Account WHERE A# = in_Ano) - in_amount) WHERE A# = in_Ano;
  out_chk := 1;
END withdraw;



 PROCEDURE deposit (in_Ano IN VARCHAR2, in_amount IN NUMBER) IS
 BEGIN
  UPDATE Account SET Balance = ((SELECT Balance FROM Account WHERE A# = in_Ano) + in_amount) WHERE A# = in_Ano;
 END deposit;


 PROCEDURE show_branch (in_Bno IN VARCHAR2, out_add OUT VARCHAR2, out_cursor OUT SYS_REFCURSOR, out_total OUT NUMBER) IS
  v_add VARCHAR2(30);
  v_total NUMBER;
 BEGIN
  SELECT Address INTO v_add FROM Branch WHERE B# = in_Bno;
  SELECT SUM(Balance) INTO v_total FROM Account WHERE A# LIKE CONCAT(in_Bno, '%');
  OPEN out_cursor FOR SELECT a.A#, a.C#, c.Name, a.Balance FROM Account a, Customer c where a.A# LIKE CONCAT(in_Bno, '%') AND a.C# = c.C# ORDER BY a.A#;
  out_add := v_add;
  out_total := v_total;
 END show_branch;


 PROCEDURE show_all_branches (out_cursor OUT SYS_REFCURSOR) IS
 BEGIN
  OPEN out_cursor FOR SELECT B# FROM Branch ORDER BY B#;
 END show_all_branches;




 PROCEDURE show_customer (in_name IN VARCHAR2, out_chk OUT INTEGER, out_Cno OUT VARCHAR2, out_cursor OUT SYS_REFCURSOR, out_total OUT NUMBER) IS
  v_Cno VARCHAR2(5);
  v_count NUMBER;
  v_total NUMBER;
 BEGIN
  SELECT COUNT(*) INTO v_count FROM Customer WHERE Name = in_name;
  IF v_count < 1 THEN
   out_chk := 0;
   DBMS_OUTPUT.PUT_LINE('Customer doesnt exist');
   RETURN;
  END IF;
  SELECT C# INTO v_Cno FROM Customer WHERE Name = in_name;
  OPEN out_cursor FOR SELECT b.Address, a.A#, a.Balance FROM Account a, Branch b WHERE b.B# = SUBSTR(a.A#, 1,3) AND C# = v_Cno;
  SELECT SUM(Balance) INTO v_total FROM Account WHERE C# = v_Cno;
  out_chk := 1;
  out_Cno := v_Cno;
  out_total := v_total;
 END show_customer;

END;
/




