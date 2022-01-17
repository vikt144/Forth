/*
  warning  <does>
  
  строка 873  - превышен номер фции >127
   d>f f>d    - оттестировать
  
*/

package forth;

import java.util.*;
import java.lang.*;
import java.lang.reflect.*;
import java.io.*;

import java.nio.*; // ByteBuffer


public class fas {


   public class STACK {
     public int [] stack = null;  //
     public double [] fstack = null;  //     
     public void initStack(int stSize) { stack=new int [stSize];} // тест присвоенный стек и инициализируемый

     public int sp,fsp;  //указатели

     public void push(int v) {stack[sp++] = v;   }  
     public int pop()        {return stack[--sp];} // --sp
     public int peek()       {return stack[sp - 1];}  // неразрушающее чтение стека
     public int peek2()      {return stack[sp - 2];	}
     public void drop(int i) {sp -= i;	}
     public int getDepth()   {return sp; }
     //                                                  float ----------------
     public void fpush(double v) {fstack[fsp++] = v; }  
     public double fpop()     {return fstack[--fsp];} // --sp
     public double fpeek()    {return fstack[fsp - 1];}  // неразрушающее чтение стека
     public double fpeek2()   {return fstack[fsp - 2];}
     public void fdrop(int i) {fsp -= i;    }
     public int fgetDepth()   {	return fsp; }
   } //endstack
 

public int  error=0;        // -1 not number
public int numberFormat=0;  // 1 int   3 float

public volatile int BLK_ = 0 ; // перенести в память
public  int _IN=0;       // !!!!to memory 
public String TIB; //  строковая переменная - иммитирует tib
public String StrBuffer;  //  сюда возвращает значение word и забирает find 

public STACK ST;// = new STACK();

//////////////////////////////////////////////////////////////world///////////////////////

public static  boolean ifblank(char ch, char separator) {   
   boolean ret=false;
   if(ch == separator) ret = true;
    else	  
      if (separator==' ')
          if(ch == ' ' || ch == '\n' || ch == '\t' || ch=='\r') ret = true;
 return ret;  
 }

static  public int skipBlank(String s, int _in, char separator) {
      int position; 
      position=_in; 
      boolean log=true;
      while (log)  
      if ( (position)==s.length() ) {position= -1; log = false; }  // достигнут конец потока возвращает -1
       else 
       {
         if  ( ifblank( s.charAt(position), separator )  ) position++;  else log=false;
         }  //else
 return position;
} //endfunk
    
 static    public int skipUntilBlank(String s, int _in, char separator) { 
      int position; 
      position=_in; 
      boolean log=true;
      while (log)  
      if ( (position)==s.length() ) {  log = false; }  // достигнут конец  
       else 
       {
         if  ( ! ifblank( s.charAt(position) , separator )  ) position++;  else { log=false;}//position++;} // WARNING !!!!1если не бланк
         }  //else
 return position;
} //endfunk
  
public   String word_( String s, char separator ) {
  String w = null;
  int start, ends;
  start=skipBlank(s, _IN , separator);  
  if (start==-1) w=""; // достигнут конец потока, возвращает слово нулевой длинны
     else {
     ends=skipUntilBlank(s, start, separator);  

     if ( separator == ' ') _IN=ends;
       else _IN=ends+1;                    // костыллб для word - 

      w=s.substring(start,ends);  
      }
return w;    
}   

public void WORD() {
  int charBlank=ST.pop();     //  
  String s=null;
  if (BLK_==0) s=TIB;
    // else  tmp 
    //   s = (String)StringVector.elementAt(BLK_);
  String si=word_(s , (char)charBlank );
  StrBuffer=null; StrBuffer=si; //  System.out.println("buff="+si);
  ST.push(0);  // если 0, то сохраняет в StrBuffer
} 
 
public void number() {  //  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! адрес строки не реализован
  numberFormat=0;
  int ind = ST.pop(); // снять адр строки 
//  ind = 0;          //   раскоментировать. если stringVector не реализован
  String s = null;
  if (ind == 0 ) 
         s = StrBuffer;
   else  s = getStringv(StringVector, ind);  // закоментировать. если stringVector не реализован
  int ii;
  try {ii = Integer.parseInt(s);
       ST.push(ii); 
       numberFormat=1;
      } catch (Exception e) { 
       
                     try { double dbl = Double.parseDouble(s);
		           numberFormat=3;
			   ST.fpush(dbl);  
			   } catch (Exception ee) {       error=-1; }
				
		  } 
 
}


///  вырезано часть 1


//////////////////////////////////////////////////////////  vectors
/* Vector - это массив объектов, который способен расширятся. 
   Хранение здесь строк и прочих объектов неопределенной длинны и кол-ва
   имеет преимущества, так как иначе бы пришлось бы выделять слишком
   большую память для форт машины, что бы учесть все неопределенности

   УЭ  - удаленый элемент
   
  адрес в векторе   значение 
   0                  6       // тут хранится адрес последнего УЭ
   1               string0   // строки
   3               string1   
   4                0         // самый первый УЭ   
   5               string3    
   6               4         // тут хранится адрес предыдущего УЭ
   7               string5   
*/
public  Vector StringVector; 

public Vector initVirtualMem(int i) {
 Vector v = new Vector();
 Integer I = new Integer(i);  // тип Integer, это объектное представление типа int
 v.addElement(I);   
 return v;
}

 
public int appendData(Vector v,Object obj) {
 
    Integer  Ind = (Integer)v.elementAt(0);   // получить значение начального элемента вектора как Integer
    int ind = Ind.intValue();                      // преобразовать в нормальный int
    
    if ( ind == 0) {                                  // если равен 0, то свободных элементов нет
      v.addElement(obj);//                    // тогда добавляем объект  к вектору
      ind=v.size()-1;
      }
        else {
          Integer I =( Integer)v.elementAt(ind);  // иначе ind указывает на свободный элемент (СЭ) 
          int i = I.intValue();                                //там хранится предыдущий  (СЭ) который получаем
    v.setElementAt(obj, ind);                  // Замещаем элемент вектора с номером ind объектом obj
    v.setElementAt(I, 0);                    // в начальный элемент вектора пишем предыдущий  СЭ
          }

  return ind;          // возвращаем индекс объекта в векторе
}

  public int removeData (Vector v, int ind){
    int ret = 0;
    if ( ind>v.size()-1) return  -1 ;
    if (getTypeData( v, ind).compareTo("java.lang.Integer")==0 )  return  -2 ;
     
    if ( ind==v.size()-1) v.removeElementAt(ind) ;
      else {
           v.setElementAt(null, ind); 
           Integer IndOld=(Integer)v.elementAt(0); // получить предыдущее знач 0 элемента
           int ind_=IndOld.intValue();  
           v.setElementAt(IndOld, ind);     // записать его в элемент который на удаление
           Integer IndNew = new Integer(ind);
           v.setElementAt(IndNew, 0);   // 0 элемент указывает на новый        
	}        
  return ret;
  } 

public String getStringv(Vector v, int ind){
  return (String)v.elementAt(ind);
}


public String getTypeData( Vector v, int ind){
return  v.elementAt(ind) . getClass().getName() ;
}




public  void ascii() {  ST.push(32); WORD();
                        ST.pop(); 
			ST.push( (int)StrBuffer.charAt(0) ) ;
}

 public void qstring() {  // s"  на верш длина 
   int N = TIB.indexOf('\"' ,  _IN);
   String s = TIB.substring(_IN,N);
   ST.push( appendData(StringVector, s )  ); //appendData возвращает индекс на строку
   ST.push( s.length() );
   _IN = N+1;    
 }

  public void printIt(String s) {
      byte[] b = null; 
      try {
      b = s.getBytes("UTF8"); //new String(utf8Bytes, "UTF8");
      String utf_ = new String(b, "UTF8"  );
      System.out.println(utf_ ); // конвертация в utf8
      } catch (Exception e ) {} 
  }

 public void stype() {  // type
   ST.pop(); //сбросить длинну
   int ind = ST.pop(); 
   String str;
   if (ind == 0) str = StrBuffer;
     else str = getStringv(StringVector, ind) ;
    printIt( str );
 }


 public void sconcat() {  //s+  s0 l0 s1 l1 -- sn ln  - соединение строк
   int l0 = ST.pop();  int s0 = ST.pop();
   int l1 = ST.pop();  int s1 = ST.pop(); 
   String str0 = getStringv(StringVector, s0);
   String str1 = getStringv(StringVector, s1);
   String str=str1+str0;  
   ST.push( appendData(StringVector, str )  ); //appendData возвращает индекс на строку
   ST.push( str.length() );
 }

  public void sequals() {  
   int l0 = ST.pop();  int s0 = ST.pop();
   int l1 = ST.pop();  int s1 = ST.pop(); 
   String str0 = getStringv(StringVector, s0);
   String str1 = getStringv(StringVector, s1);
   if (str1.equals(str0) )   
                    ST.push( -1 ); 
      else
       ST.push( 0 );
 }

 public void spick() {  // adr length   --  adr1 len1   дублирует строку
       int l = ST.pop();  int sAdr = ST.pop();
       String str;
       if (sAdr==0) str = StrBuffer; 
          else  str = getStringv(StringVector, sAdr);
       ST.push( appendData(StringVector, str )  ); 
       ST.push( str.length() );
 }



////////////////////////////////////////////////////////////////////////////////////

public     int here;  // адрес переменной HERE в массиве memory
public     int latest; // адрес переменной LATEST в массиве memory
public     int state;

public     int context ;
public     int current  ;
public     int forthVoc ; 

 public ByteBuffer mem;// = ByteBuffer.allocate( 10000  /* max capacity */ );

 public ByteBuffer createMemory(byte[] B , int size) {
   int sz=10000;  //память по умолчанию
   ByteBuffer bb=null;
   if (B==null) {
      if (size!=0) sz=size;         // создание нового образа памяти 
      bb=ByteBuffer.allocate( sz  /* max capacity */ );
      }
    else 
      bb=ByteBuffer.wrap( B );  // создание на основе существ. массива байт
 return bb;
 } 


/// -->

 
///<---

/* 
Создает в памяти
link  int               0  - указатель на поле link предыдущей статьи  
flag  byte              4  - поле имени - флаги
size  int  -            5  - поле имени -   длинна строки 
adr   int               9  - поле имени - - индекс на строку или адрес строки
cfa   int               13 - cfa
*/

 public int newDict(String name, int prevAdr,byte flags) { // создает статью, возвращает адрес новой статьи
   int linkToThisDict = mem.getInt(here); // получить here   
   compile_int (prevAdr );  //  - указатель на предыдущую статью 
   compile_byte( flags);    
   compile_int ( name .length() );  
   int adr = appendData(StringVector, name) ;
   compile_int (adr);
   compile_int (0);   // в cfa записывается 0, что означает, что при исполнение слова, на стеке будет pfa   
   return linkToThisDict;
 }

 public  void cre0(String s) {   // временная вспомогательная ф-ция. создающая упрощенную запись в словаре 
   String STR = s. toUpperCase(); // реализовать проверку на ""  // 
//   int hereValue = mem.getInt(here); //short tmp = memory[here]; //получить here   
   int currentAdr   = mem.getInt(current);
   int prv = mem.getInt(currentAdr);        //указатель на предыдущую статью 

   int len = STR.length();

   int hereValue = newDict(STR, prv, (byte)0);

   mem.putInt(latest,hereValue);
   mem.putInt(currentAdr, hereValue);

 }

 public  void cre1() { 
  ST.push(32); 
  WORD();
  ST.pop();            // сброс стека, world пока возвращает только 0
  cre0(StrBuffer);
 }


public void setCFA() {  /////////////////////////////////////////////*
  int i = ST.pop();     // снять со стека нужное значение cfa 
  ST.push(  mem.getInt(latest)  );  //int tmp = mem.getInt(latest);
  Link_();
  int aCFA = ST.pop(); 
  mem.putInt(aCFA , i);
}

 public  void immediate() {
   ST.push(  mem.getInt(latest)  );
   L_name();
   int tmp = ST.pop(); 
   mem.put(tmp, (byte)1 );
//  System.out.println("set imediate " );
 }
 
public void L_name() {   // - на стеке  адрес Link возвращает на стек адр поля имени
    int i = ST.pop(); 
    ST.push(i+4);
} 

public void name_() {  // name --> cfa
    int i = ST.pop(); 
    ST.push(i+9);
}

public void Link_() {  // link --cfa
       int i = ST.pop(); 
       ST.push(i+13);
}

public void _body() {  // cfa -- pfa
  int i = ST.pop();
  ST.push(i+4);
}


/*
ИСКАТЬ СЛОВО Т В ТЕКУЩЕМ КОНТЕКСТЕ ЕСЛИ N=0, ТО А=Т И СЛОВО НЕ НАЙДЕНО, ИНАЧЕ A=CFA НАЙДЕННОЙ СТАТЬИ, N=1 ДЛЯ СЛОВ "IMMEDIATE" И N=-1 ДЛЯ ОСТАЛЬНЫХ
*/
 //поле кода   n 0 - не найдено  если не отрицательно то immediate 
public void FIND0(int cntxt) {  //переменная cntx - адрес ( link ) последней словарной статьи в данном context
 int ret=0;
 boolean bool=false;
 ST.pop(); // сброс стека, ----> 0 тут должен анализ ecли  0 то  s=StrBuffer иначе из вектора  var fff  10 fff !  fff @ .
 String s=StrBuffer. toUpperCase();
 String si = null;
  boolean immediate=false;
 int tmp0 = mem.getInt(cntxt); //int tmp=memory[latest];
 int tmp  =  mem.getInt(tmp0);
 while (tmp != 0 && ! bool) {
   immediate=false;     // заменить на анализ битов
   ST.push(tmp); 
   L_name();   
   int nameField = ST.pop();       //  получить адр поля имени 
   byte  flag = mem.get(nameField); //  получить флаг
   if (flag /*len<0 */ !=0) {immediate=true;} // если длинна слова отрицательна  - immediate=true; 
   int ind = mem.getInt( nameField+5);// tmp+9);//  nameField+5);//    // получаем инд в векторе  	( nameField+1  длинна строки)    
//   System.out.println("ind "+ ind);
   si = (String)StringVector.elementAt(ind);
   if ( s.compareTo(si)==0) {
      bool = true;
      ST.push(nameField);   
      name_();  // на стеке cfa      
      ret = ST.pop(); // tmp+13;//mem.getInt(tmp+13);// tmp+3; //cfa
      }                     
    else
    tmp= mem.getInt(tmp);  //(memory[tmp]; 
 
 }//while  
 
 ST.push(ret);
 if (ret==0) ST.push(0);
         else 
	 if (   ! immediate) ST.push(-1);  
	   else {ST.push(1);  /* System.out.println("immediate!! "+si )  */ }
 
}  

public void FIND() {
  FIND0(context);
  
  int i = ST.pop();

  if ( i ==0 && current != context ) { //если не найден в контексте
        FIND0(current);
	i = ST.pop();
       }
  if ( i ==0 && current != forthVoc && context != forthVoc)  {
        FIND0(forthVoc);
	i = ST.pop();
        }  
  ST.push(i) ; //вернуть стек
 
 }

/// вырезано часть вторая



////////////////////////////////////////////////////////////////////////////////////FVM////////////////////////////////

 
/* Part of the project take  from retro forth
http://forthworks.com/retro/
*/


public class FVM {
 
        private int sp = 0, rsp = 0, ip = 0; // указатели стека, стека возвратов и адрес команды
 
        public STACK stack,adrStack;    //стеки  

        public int [] ports;  // порты ввода вывода


  public static final int
    VM_NOP = 0 ,
    VM_LIT = 1 ,  // положить 16 битное значение на стек
    VM_DUP = 2 ,    VM_DROP = 3 ,  VM_SWAP = 4 ,
    VM_PUSH = 5 ,    VM_POP = 6 ,
    VM_CALL = 7 ,    VM_JUMP = 8 ,    VM_RETURN = 9 , 
    VM_GT/*_JUMP*/ = 10 ,VM_LT/*_JUMP*/ = 11 , VM_EQ /*VM_NE_JUMP*/ = 12 ,  VM_EQ_JUMP = 13 ,  //  >  <  =  =+jump
    VM_FETCH = 14 ,  VM_STORE = 15 ,  // @  !
    VM_ADD = 16 ,    VM_SUB = 17 ,    VM_MUL = 18 ,  VM_DIVMOD = 19 , // + - *  / 
    VM_AND = 20 , VM_OR = 21 , VM_XOR = 22 ,

    VM_SHL = 23 ,    VM_SHR = 24 ,    VM_ZERO_EXIT = 25 ,
    VM_INC = 26 ,  VM_DEC = 27 ,
    VM_IN = 28 ,  VM_OUT = 29 ,  VM_WAIT = 30, // записсь чтение портов , ожидание
   
    VM_LIT8 = 31 ,// положить 8 битное значение на стек
    VM_FETCH8 = 32 ,  VM_STORE8 = 34,  VM_FLIT = 33 // c@      c! flit 
     ;

  private void handleDevices() {  // от ретро, // вызывается командой wait
        //зависит от реализации
  }


  public Object  Ob; 
 private void callService(int port, int y) { // вызывается во время записи в порт (out)
        // зависит от реализации
   
  if (port==8)  { /* System.out.println("port=8 ") */ ;runProcedure( Ob , procedureLists[y]);
  }
  else 
   if (port != 0) System.out.println("нетот порт "+ port);
     else
     {
     switch (y) {
       case 0 : _HALT=true; break;
       case 1 : System.out.print( /*"st= "+ */stack.pop()+" " ); /*_HALT=true; */ break;
 //      case 2 :  proc(); /* _HALT=true; */  break;
       } //switch
   }//else
 }
 

  public boolean _HALT =  false;  //Переменной Halt присваиваются значение во время выполнения
                                //callService, при исполнении инструкции out

 public void processImage( int startIP ) {
  // ip=startIP;
  _HALT=false;
  mem.position(startIP);
  while  ( /*ip<IMAGE_SIZE  && */ ! _HALT) {
      process(); 
     // ip++;
     }
  } //void
  

  private void process() {
      int x, y, z,tmp, op ;
   op=mem.get();      //  System.out.println("cod="+op);
   switch(op) {
    case VM_NOP:
      break;
    case VM_LIT:    stack.push(   mem.getInt( /* mem.position() */ )   )  ;  //!!!!!!!!!!!!!!!!!!  
      break;
    case VM_DUP:    tmp=stack.pop(); stack.push(tmp);stack.push(tmp);
      break;
    case VM_DROP:  stack.drop(1);
      break;
    case VM_SWAP:  x = stack.pop(); y= stack.pop(); stack.push(x);stack.push(y);
      break;
    case VM_PUSH:  x = stack.pop();  adrStack.push(x); // со стека данных на адресный стек >R
      break;
    case VM_POP:    x = adrStack.pop(); stack.push(x); // обратно  R>
      break;

    case VM_CALL:   x = mem.getInt(); adrStack.push(  mem.position() )  ;   mem.position(x);//-1);// !!!    ///ip++; adrStack.push(ip); ip=image[ip]-1;
      break;
    case VM_JUMP:  x = mem.getInt();    mem.position(x);//-1);   //  ip++;    ip = image[ip]-1;
      break;
    case VM_RETURN: //int iie=adrStack.pop();System.out.println("adr return=" + iie);mem.position( iie);
                    mem.position( adrStack.pop() ) ; //  ip = adrStack.pop();
      break;

    case VM_GT : //    //    0 В ФОРТЕ -- FALSE 
      x = stack.pop();  if (x>0) x=-1; else x=0; stack.push(x);
      break;
    case VM_LT :
      x = stack.pop();  if (x<0)  x=-1; else x=0 ; stack.push(x);      
      break;
    case VM_EQ : // ip++;
      x = stack.pop();  if (x==0)  x=-1; else x=0 ; stack.push(x);
      break;
    case VM_EQ_JUMP:  y = mem.getInt();  x = stack.pop(); 
                      if (x==0) mem.position(y);//-1);  //ip++;  x = stack.pop(); if (x==0) ip = image[ip] - 1;     
      break;

   
    case VM_FETCH: /// @
      tmp=stack.pop();      
      x=mem.getInt(tmp);    
      stack.push(x);        
      break;               
    case VM_STORE:  //    
       x = stack.pop(); y= stack.pop();  
       mem.putInt(x,y);    
      break;

    case VM_ADD:    x = stack.pop(); y= stack.pop();stack.push(y+x);
      break;
    case VM_SUB:    x = stack.pop(); y= stack.pop();stack.push(y-x);
      break;
    case VM_MUL:    x = stack.pop(); y= stack.pop();stack.push(y*x);
      break;
    case VM_DIVMOD:    //my relize div 0
      x = stack.pop(); y= stack.pop();   
      stack.push(y % x);  stack.push(y/x); // на стеке остаток и целое
      break;    // иожно добавить проверку /0 если не отлавливаются исключения
    case VM_AND:    x = stack.pop(); y= stack.pop();stack.push(x & y);
      break;
    case VM_OR:      x = stack.pop(); y= stack.pop();stack.push(x | y);
      break;
    case VM_XOR:  x = stack.pop(); y= stack.pop();stack.push(x ^ y);
      break;
    case VM_SHL:    x = stack.pop(); y= stack.pop();stack.push(  y << x );  // сдвиг битов влево
      break; 
    case VM_SHR:  x = stack.pop(); y= stack.pop();stack.push( y >>= x );  //  вправо
      break;
    case VM_ZERO_EXIT:    // выход из подпрограммы, если на стеке 0 -- не используется
      if (stack.peek() == 0)
      {
        stack.drop(1);
        ip = adrStack.pop();
      }
      break;
    case VM_INC:  x = stack.pop(); x++; stack.push(x);
    break;
    case VM_DEC:  x = stack.pop(); x--; stack.push(x);
    break;

  case VM_IN:          // чтение порта
      x = stack.pop();
      y = ports[x];
      stack.push(y);
      ports[x] = 0;
      break;
    case VM_OUT:  // вершина номер порта
      x=stack.pop();
      y=stack.pop();
      ports[x]=y;//
      callService(x,y);  //вызов внешней функции
      break;
    case VM_WAIT:  handleDevices(); // наследство от  ретро
      break;
    case VM_LIT8:  // положить 8 битное значение
       stack.push(   mem.get()   )  ;
      break;

    case VM_FETCH8: /// c@
      tmp=stack.pop();                        // со стека снимается адрес
      x=mem.get(tmp);  
      stack.push(x ); 
      break;          

    case VM_STORE8:  // c!   //  
      x = stack.pop(); y= stack.pop(); //
      mem.put(x,(byte)y);
      break;

    case VM_FLIT:
      stack.fpush(   mem.getDouble()   )  ;      
       break;      

        }
   }	
    
//                                                            reflections api
 public Method [] procedureLists = new Method[100];
 int procedureIndex=0;

 public Method registerProcedure( Object o, String procName) {
     Method method=null;
     try {
       method = o.getClass().getDeclaredMethod( procName );
       method.setAccessible(true);
     } catch (NoSuchMethodException e )   
        {
         e.printStackTrace();
         }   
 return method;  
 } 
 
 public void buildProcedure( Object o, String procName) {
      Method method;
      method=registerProcedure(o, procName);
      int index = procedureIndex;
      procedureIndex++;
      procedureLists[index]=method;
      cre0(procName);
      setout_( index /* functNum */ , 8 /*int portNum */, true /*boolean ret*/ );       
 }

//https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Method.html#invoke-java.lang.Object-java.lang.Object...-

 public void runProcedure(Object o, Method method) {
     try {
        method.invoke(o);
     } catch ( // NoSuchMethodException | 
               InvocationTargetException | IllegalAccessException e) 
        {
         e.printStackTrace();
         } 
  }
						       
 }  //
/////fvm


/// public String ref = "ginger";
/// public int refi = 3;
 
public void print() { System.out.println(StrBuffer); }

 public FVM VM;

 public int exit_addr;  //адрес процедуры выхода

 public String initwords =
// " start " +   
  " NOP 0  -1      LIT  1 -1      dup  2  -1    drop  3  -1  swap  4  -1 " + 
 "  >R  5  -1      R>   6 -1      CALL  7  -1    branch  8 -1   ;  9 1 "   +
   "  0>   10 -1   0<  11 -1       0=  12  -1     ?branch  13  -1 "        +
   "  @  14  -1   !  15  -1      "                                         +
  "   +  16  -1   -  17 -1   *  18  -1  /MOD  19  -1 "                     +
  "   AND  20   -1   OR  21 -1   XOR  22  -1 SHL  23  -1  SHR  24 -1 "     +
  "   ZERO_EXIT  25 -1    1+  26  -1   1-  27 -1 "        +
  "   IN  28  -1  OUT  29 -1    WAIT  30  -1 "            +
 "    LIT8  31 -1  c@ 32 -1  flit 34 -1   c! 33 -1 "  + // endpart -111 -1 "   +

 " EXIT . " + 

//   fort_name  java_void_name  i/n  - immediate or not

" : proc n        ; ret i       create cre1 n     allot allot n       does> does_  n      , comma n    here here n " + 
"   word WORD n   find FIND n  print print n   literal literal n" +
 
 " compile compile  n    compile_call compile_call n    immediate immediate n    >resolve resolve_ n   <resolve _resolve n " +  
 " name> name_ n   L>NAME L_name n      link> Link_  n  >body _body n  state@ getState n " +  

 "     (  comment  i    "   + 
 " ascii ascii n    s\" qstring i    type stype n  .\" qtype  i  s+ sconcat n    spick spick n" +
 " s= sequals n " +

//float words
 " fdup fdup n   fswap fswap n   fdrop fdrop n " +
 " f.  fprint_ n  f! fstore n  f@ fload n     f+  fadd n   f- fsub n       f* fmul  n   f/ fdiv n "  +
 " f> fgreater n   f= fequals  n    f< fless n   f<> fnotequals n   f<= flessORequal n  f>= fgreaterORequal n  " +
 " fsin fsin n    fcos fcos n  ftan ftan n  fasin fasin n  facos facos n  fatan fatan n  fatan2 fatan2 n" +
 " fln fln  n   flog flog n    fsqrt fsqrt n   fexp fexp n   fabs fabs n  floor floor n  d>f longToFloat n   f>d floatToLong n "  + //   

 "   ;;   " +
//  */
 " : <   - 0< ; \n  : =   - 0= ;  \n  : >  - 0> ;  \n \n \n " +
 ": >mark here 4 allot ;  \n  : <mark here ; \n \n \n " + 

 " : if compile ?branch  >mark ; immediate \n" +
 " : then >resolve ; immediate \n " +
 " : else   compile branch  >mark swap >resolve ; immediate \n \n " +

 " : begin <mark ; immediate \n" + 
 " : until compile ?branch <resolve ; immediate \n " +
 " : while  compile ?branch  >mark   ; immediate \n " +
 " : repeat  compile branch  swap <resolve  >resolve ; immediate \n \n \n" +

 " : var   create 4 allot   ;  \n  : const create , does> @ ; \n\n " +
 
 " var tmpregistr0  var tmpregistr1  var tmpregistr2  \n\n "  +
 
 " : over swap dup  tmpregistr0 ! swap tmpregistr0 @   \n ; " +   
 " : rot  tmpregistr0 ! tmpregistr1 !  tmpregistr2 !       tmpregistr1 @  tmpregistr0 @ tmpregistr2 @ ; \n" + 
  " : ?DUP ( A -> A,A/0 )  DUP  IF DUP THEN ; \n" + 
 " : R@ R> dup >R ; \n" + 

 " :  /   /mod swap drop ; \n" + 
 " : mod  /mod drop ; \n" + 
  
 " : 2+ 1+ 1+ ; \n" + 
 " : 2- 1- 1- ; \n" + 
 " : 2/ 2 /mod swap drop ; \n" + 
 " : negate 0 swap - ; \n" + 
 " : ABS ( A --->абс A ) DUP 0< IF NEGATE THEN ; \n" + 

 " : 2dup    tmpregistr0 ! tmpregistr1 !     tmpregistr1 @ tmpregistr0 @    tmpregistr1 @ tmpregistr0 @  ; \n" + 
 " : 2drop  drop drop ; \n" +
 " : 2var create  8 allot ;  \n" +
 " : 2!  dup tmpregistr0 ! ! tmpregistr0 @ 4 + ! ;   \n" +
 " : 2@  dup @ swap 4 + @  swap ;   \n"

 ;   

  public long int2long()  {int int0 = ST.pop(); int int1 = ST.pop();
                           return (int0 << 32 | int1 & 0xffffffff) ; 
  }
  public void long2int(long l)  {int int0 = (int)l ; int int1 = (int)(l>>32);
                                 ST.push(int1) ; ST.push(int0) ;
  }  

  public void longToFloat(){ ST.fpush( (double)int2long() );  } //    
  public void floatToLong(){  long2int( (long) ST.fpop()  );  } 

//  fdup fswap fdrop
//  f.  f! f@  f+ f-  f*  f/   f> f< f= f<> f>= f<=
// fsin fcos ftan  fasin facos fatan fatan2 
// fln flog  fsqrt fexp fabs floor  d>f f>d

  public void  fdup () { double tmp = ST.fpop(); ST.fpush(tmp); ST.fpush(tmp); }   
  public void  fswap() { double tmp = ST.fpop();  double tmp1 = ST.fpop(); ST.fpush(tmp);ST.fpush(tmp1);}
  public void  fdrop() { double tmp = ST.fpop();}

  public void fprint_()  {
    System.out.println("dbl=" + ST.fpop() );
  }
  public void fstore() {
     int adr = ST.pop();
     mem.putDouble(adr, ST.fpop() ) ;
  }
  public void fload()  {
      int adr = ST.pop();
      ST.fpush( mem.getDouble(adr) ); 
  }
  
  public void fadd()  {  double f1,f2;  f1=ST.fpop(); f2=ST.fpop(); ST.fpush(f2+f1); } 
  public void fsub()  {  double f1,f2;  f1=ST.fpop(); f2=ST.fpop(); ST.fpush(f2-f1); }   
  public void fmul()  {  double f1,f2;  f1=ST.fpop(); f2=ST.fpop(); ST.fpush(f2*f1); } 
  public void fdiv()  {  double f1,f2;  f1=ST.fpop(); f2=ST.fpop(); ST.fpush(f2/f1); } 

  public void fgreater(){  double f1,f2;  f1=ST.fpop(); f2=ST.fpop();  // f>
                           if ( (f2 - f1) > 0 )  ST.push(-1);    //true
				else  ST.push(0);  //false
   } 

  public void fequals(){  double f1,f2;  f1=ST.fpop(); f2=ST.fpop();  // f=  
                           if ( (f2 - f1) == 0 )  ST.push(-1);    //true
				else  ST.push(0);  //false
   }    
  public void fless(){  double f1,f2;  f1=ST.fpop(); f2=ST.fpop();  // f< 
                           if ( (f2 - f1) < 0 )  ST.push(-1);    //true
				else  ST.push(0);  //false
   }     
  public void  fnotequals (){  double f1,f2;  f1=ST.fpop(); f2=ST.fpop();  // f<>
                           if ( (f2 - f1) != 0 )  ST.push(-1);    //true
				else  ST.push(0);  //false
   }     
  public void  flessORequal(){  double f1,f2;  f1=ST.fpop(); f2=ST.fpop();  // f<=
                           if ( (f2 - f1) <= 0 )  ST.push(-1);    //true
				else  ST.push(0);  //false
   }      
  public void  fgreaterORequal(){  double f1,f2;  f1=ST.fpop(); f2=ST.fpop();  // f>=
                           if ( (f2 - f1) >= 0 )  ST.push(-1);    //true
				else  ST.push(0);  //false
   }   

  public void fsqrt()  {  ST.fpush( Math. sqrt(  ST.fpop()  )  ) ; } 
  public void fexp()  {  ST.fpush( Math. exp  (  ST.fpop()  )  ) ; } 
  public void fln()  {  ST.fpush( Math. log   (  ST.fpop()  )  ) ; }  // натуральный логарифм 
  public void flog()  {  ST.fpush( Math. log10(  ST.fpop()  )  ) ; } 
  public void fabs()  {  ST.fpush( Math. abs(  ST.fpop()  )  ) ; } 
  public void floor()  {  ST.fpush( Math. floor (  ST.fpop()  )  ) ; }   

  public void fsin()  {  ST.fpush( Math. sin (  ST.fpop()  )  ) ; }
  public void fcos()  {  ST.fpush( Math. cos (  ST.fpop()  )  ) ; } 
  public void facos()  {  ST.fpush( Math. acos (  ST.fpop()  )  ) ; } 
  public void fasin()  {  ST.fpush( Math. asin (  ST.fpop()  )  ) ; } 
  public void ftan()   {  ST.fpush( Math.  tan (  ST.fpop()  )  ) ; }      
  public void fatan()  {  ST.fpush( Math. atan (  ST.fpop()  )  ) ; }   
  public void fatan2() {  ST.fpush( Math. atan2(  ST.fpop() ,  ST.fpop()   )  ) ; } // проверить порядок данных в стеке
    
 public void initFVMwords() {
   TIB = null; TIB = initwords;
   _IN=0;
  boolean enddo=false; //для выхода из цикла
  do {
      ST.push(32);  WORD();  // на стеке 0    
      ST.pop();  // сброс стека
      String  name  = StrBuffer;
      cre0(name);
      ST.push(32); WORD(); // opcode
      ST.pop();  // сброс стека
      int code=0;
      try { code=Integer.parseInt(StrBuffer);} 
          catch (Exception e){}
     // ST.push(code); comma16();  // компилипуем opcode
      int tmp = mem.getInt(here);
      mem.put(tmp,(byte)code);  // mem.putInt(tmp,(byte)code);
      ST.push(32); WORD(); // immediate
      ST.pop();  // сброс стека         пропускаем immediate

      ST.push(1);  setCFA();   

      mem.put(tmp+1,(byte)9); //      mem.putInt(tmp+1,(byte)9);
      mem.putInt(here,tmp+2);      
            
      if (name.compareTo("c!") == 0)
                            enddo=true;
      } while (! enddo) ;		    
  }
 
 
 public void setout_( int functNum, int portNum , boolean ret) { // создает последовательность кодов " lit8 functNum  lit8 portNum "
      int tmp = mem.getInt(here);                   // получить here
      mem.put(tmp,(byte)31);       tmp++;        // записать код 31 "lit8"
      mem.put(tmp,(byte)functNum); tmp++;        //warning переписать на случай   functNum> 127  
      mem.put(tmp,(byte)31);       tmp++;    ///////////////////////////////////////////////переделать mem.putInt mem.put
      mem.put(tmp,(byte)portNum ); tmp++;  
      mem.put(tmp,(byte)29); tmp++;        //out
      if (ret)  {  
          mem.put(tmp,(byte)9);    
	  tmp++; }   // 9 - код операции ";" возврат из подпрограммы 
      mem.putInt(here,tmp);                  // обновить here
 }
 



 public void initEXTwords_2(Object o) {           //  перенести вниз перед comma
    boolean enddo=false; //для выхода из цикла
    boolean endparam=false;
 
    do {
      ST.push(32);  WORD();  // на стеке 0    
      ST.pop();  // сброс стека
      String  name  = StrBuffer;
      if ( ! name.equals(";;") ) {                //если слово не ;;  то
               //  VM.buildProcedure(  o, name );  //
		 cre0(name);
                 ST.push(32);  WORD();  // на стеке 0    
                 ST.pop();  // сброс стека
                 String  proc_name = StrBuffer;
		 Method method = VM.registerProcedure(o, proc_name);
                 int index = VM.procedureIndex;
                 VM.procedureIndex++;
                 VM.procedureLists[index]=method;
                 setout_( index /* functNum */ , 8 /*int portNum */, true /*boolean ret*/ );       
                 ST.push(32);  WORD();  // на стеке 0    
                 ST.pop();  // сброс стека
		      
                 String  imm_test  = StrBuffer;
                 if ( imm_test.equals("i") ) 
		                       immediate();
		 ST.push(2);  setCFA();      // cfa = 2     
              }
          else  enddo=true;	 
    } while (! enddo) ;	      
 }




public void exec () {
  int adrCfa=ST.pop();  

  int cfa = mem.getInt(adrCfa);  // System.out.println("ad="+ad);
  ST.push(adrCfa);
  _body();
  int body = ST.pop();
  if (cfa == 1 || cfa == 2 ) {    // если cfa 1 2
    VM.adrStack.push(exit_addr );  // положить на стек возвратов адрес процедуры exit
    VM.processImage(body ); //+4 scip cfa 
    }
    else  

      if (cfa == 0) ST.push(body);  //+4  если 0 положить adr pfa
      
       else 
         if (cfa < 0 )  {    // слова - определенные через does>
	//  System.out.println("cfa=" + ad);
	   ST.push(body);  //  положить adr pfa
	   VM.adrStack.push(exit_addr/*-1*/);
	  // int does_cfa = -cfa;
	   VM.processImage( -cfa + 1) ; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                          // -cfa связано с does и с инстрциями вирт. машины call и ret
	   } 
}//void


/// вырезано часть 3

//  компилируют byte int double и перемещают соответственно here
 public void compile_byte(byte b) {
      int tmp = mem.getInt(here);
      mem.put(tmp, b) ;
      tmp++;
      mem.putInt(here,tmp); 
 } 

 public void compile_int (int  i) {
      int tmp = mem.getInt(here);
      mem.putInt(tmp, i) ;
      mem.putInt(here,tmp+4); 
 } 

 public void  compile_Double (double dbl) {
      int tmp = mem.getInt(here);
      mem.putDouble(tmp, dbl) ;
      mem.putInt(here,tmp+8);  
 }
///////////////////////////////////////


 public void comp  () { // на стеке адр cfa
   int adrCfa=ST.pop();
   ST.push(adrCfa);
   _body();
   int body = ST.pop();  
   
   int cfa = mem.getInt(adrCfa); //  Содержимое cfa 
 
   if (  cfa== 2 ) { // если cfa = 2    компиляция вызова процедуры
              compile_byte(( byte) FVM.VM_CALL  ); // opcode call
	      compile_int (body);// mem.putInt(tmp,addr+4);  	       
	       }   
	       else  
	       if  (  cfa == 1 ) {        //    компиляция опкода  виртуальной машины    
		    byte code = mem.get(body);
		    compile_byte(code);
		       }   
		       else 

		       if  (   cfa < 0 )  {    // компиляция определения через does>
		          compile_byte( (byte) FVM.VM_LIT ); // код команды lit  
		      	  compile_int (body);  		       
		       
                          compile_byte( (byte) FVM.VM_CALL ); //  код команды call  
	                  //int i = -memory[addr]+1;///   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			  compile_int (- cfa);   //ST.push(i); comma16();		             

			    }
			   else
			    if  (   cfa== 0 )  {
		                compile_byte( (byte) FVM.VM_LIT ); // код команды lit  
		                 compile_int (body);  	
			       }
  }


 public void literal() { 
  if ( mem.getInt(state) ==1 ) {   
    compile_byte( (byte) FVM.VM_LIT );  //ST.push( FVM.VM_LIT) ;   comma16(); // compile lit
    compile_int ( ST.pop() )         ; //   comma16();                       // compile ","
   }
 }    

 public void fliteral() { 
  if ( mem.getInt(state) ==1 ) {   // 
    compile_byte( (byte) FVM.VM_FLIT );   // compile flit
    compile_Double ( ST.fpop() )      ;   
   }
 }   

public void proc() {
   // добавить тестирование пустого слова ""
//    System.out.println(" create ");// +str);
   cre1();
   ST.push(2) ; //   установить cfa = 2
   setCFA();
   mem.putInt(state,1);  // перевод в режим компиляции
} 


public void ret()  { 
  compile_byte( (byte) FVM.VM_RETURN );  // ST.push( VM.VM_RETURN ); //вкомпилировать return
  mem.putInt(state,0); 
}

public void here() {
  ST.push( mem.getInt(here) ) ; 
}

// -----

public void does_() {
 int i = VM.adrStack.pop();  // сохранить  со стека возвратов значение, которое занесло туда вызов does 
 ST.push ( -i + 1 );                                                                   //!!!!!!!!!!!!!!!!!!!1 проверить +1
 setCFA();  //   устанавливает отрицательный адрес места после вызова does в cfa   
 VM._HALT=true;   // остановить VM

VM.adrStack.pop(); //?????????????????????????????????  очистить стек возвратов . возможно чистит exit
}


public void compile() { 
   int i = VM.adrStack.pop(); // сохранить  со стека возвратов значение, которое занесло туда вызовc ompile()
   byte code = mem.get(i);  //  
   compile_byte(code);     // 
VM.adrStack.push(i+1);  // обойти следущую после compile инструкцию 
}

public void compile_call() {   // компилирует вызовы процедуры  call adr
   int i = VM.adrStack.pop(); // сохранить  со стека возвратов значение, которое занесло туда вызовc ompile()
   byte code = mem.get(i);  //  
   compile_byte(code);
   int adr =  mem.getInt(i+1);     
   compile_int(adr);        // 

VM.adrStack.push(i+5);  // обойти следущую после compile инструкцию 
}


public void resolve_() {
   int i = ST.pop();  // 
   here(); 
 //  int m = ST.pop();
   mem.putInt( i, ST.pop()  );   // memory[i] = (short)ST.pop();
//   ST.push(i);
//   comma16();
}
public void _resolve() {
  comma() ; // comma16();
} 


public void allot() {
  //memory[here]+=ST.pop(); 
  int i = mem.getInt(here) + ST.pop();
  mem.putInt(here,i); 
} 

public void comment() {
  boolean log = true;
  while (log) {
    ST.push(32); // разделитель для word  
    WORD(); 
   ST.pop();  // сброс word     
    if ( StrBuffer.compareTo( ")" )==0  ) log = false; 
  }//while
}

public void comma() {
  compile_int(ST.pop() );
}


 public void setState() {
    mem.putInt(state,ST.pop() );
 }
 public void getState() {
  ST.push(   mem.getInt(state)  );
 }
 

 public void interpret() {
  boolean log = true;     
  while ( log ) { 
    String wrl; 
    ST.push(32); // разделитель для word  
    WORD();    // на стеке 0
    wrl = StrBuffer;  //  
//    System.out.println("слово " +  wrl );
    if (wrl=="") { 
                 log = false;  //  достигнут конец потока 
                 ST.pop();  // сбросить стек после word 
		}
              else { 
	          FIND();                // на стеке n  и 
                  int n = ST.pop();     // снять со стека признак немедленного исполнения
//    System.out.println("n= " +  n );
        	  if (n==0) {         //слово не найдено - проверить если число
		           number(); //забирает со стека <адр строки> -  пока  0
		//	   System.out.println("проверка число ");
		           if (error != 0)  {   // если число - number оставляет 0
			                   log = false;
		 	  		   System.out.println("слово " +  wrl + " не найдено" );
					   error = 0;
					   } else {  // literal() ; 
		//			       System.out.println("число " +  wrl + " на стеке" );
					          if (numberFormat==1)  literal() ;
						     else if (numberFormat==3) fliteral(); 
					        }
 			      }
		    else  
                      if ( mem.getInt(state)==0 || n==1 ) // если find вернул immediate или состояние исполнения
			    { exec(); //  System.out.println("exec");
			    }
			      else 
			     { comp();
			     // System.out.println("compiling");
			     }
	      }//elsefind
  }// while
 }//interpret
 
 public String loadTextFile(String filename) {
   String ret = null;
   StringBuilder sb = new StringBuilder();
   try { 
            FileInputStream fis;
            fis=new FileInputStream(filename);
          //  System.out.println("Размер файла: " + 
          //                      fis.available() + " байт(а)");
            int i = -1;
            while(( i = fis.read()) != -1){
              sb.append((char)i );  //   System.out.print((char)i);
               }
            ret=new String(sb);
	    fis.close();
        } catch(IOException e){
            System.out.println(e.getMessage());
        }
 return ret;
 }

} 
//////////////////////////end all////////////////////////////////////
