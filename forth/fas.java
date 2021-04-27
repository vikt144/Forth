package forth;

import java.util.*;
import java.lang.*;
import java.lang.reflect.*;
import java.io.*;

////////////////////////////////реализация WORD//////////////////////////////////////////



public class fas {

   public class STACK {
     public int [] stack = null;  //
     public void initStack(int stSize) { stack=new int [stSize];} // тест присвоенный стек и инициализируемый
     public int sp;

     public void push(int v) {stack[sp++] = v; } //проверить sp++

     public int pop()        {return stack[--sp];} // --sp
	
     public int peek() { return stack[sp - 1];}  // неразрушающее чтение стэка
	
     public int peek2() {	return stack[sp - 2];	}
	
     public void drop(int i) {sp -= i;	}

     public int getDepth() {	return sp; }

   } //endstack
 

public int  error=0; // -1 not number

public volatile int BLK_ = 0 ; // перенести в память
public  int _IN=0;       // !!!!to memory 
public String TIB; //  строковая переменная - иммитирует tib
public String StrBuffer;  //  сюда возвращает значение word и забирает find 

int [] ii = new int[100];
public STACK ST;// = new STACK();
//   ST.stack = STACK. siack = new int[100];

public static  boolean ifblank(char ch) {
   boolean ret=false;
   if(ch == ' ' || ch == '\n' || ch == '\t' || ch=='\r') ret = true;
 return ret;  
 }

static  public int skipBlank(String s, int _in) {
      int position; 
      position=_in; 
      boolean log=true;
      while (log)  
      if ( (position)==s.length() ) {position= -1; log = false; }  // достигнут конец потока возвращает -1
       else 
       {
         if  ( ifblank( s.charAt(position))  ) position++;  else log=false;
         }  //else
 return position;
} //endfunk
    
 static    public int skipUntilBlank(String s, int _in) { 
      int position; 
      position=_in; 
      boolean log=true;
      while (log)  
      if ( (position)==s.length() ) {  log = false; }  // достигнут конец  
       else 
       {
         if  ( ! ifblank( s.charAt(position))  ) position++;  else log=false; //если не бланк
         }  //else
 return position;
} //endfunk
  
public   String word_( String s ) {
  String w = null;
  int start, ends;
  start=skipBlank(s, _IN);  
  if (start==-1) w=""; // достигнут конец потока, возвращает слово нулевой длинны
     else {
     ends=skipUntilBlank(s, start);  
     _IN=ends;
      w=s.substring(start,ends);  
      }
return w;    
}   

public void WORD() {
  int charBlank=ST.pop();     // !! снятие со стека blank -- пока не используется
  String s=null;
  if (BLK_==0) s=TIB;
    // else  tmp 
    //   s = (String)StringVector.elementAt(BLK_);
  String si=word_(s);
  StrBuffer=null; StrBuffer=si;
  ST.push(0);  // если 0, то сохраняет в StrBuffer
} 
 
public void number() {  //  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! адрес строки не реализован
  int ind = ST.pop(); // снять адр строки 
  String s = StrBuffer;
  int ii;
  try {ii = Integer.parseInt(s);
       ST.push(ii); 
      } catch (Exception e) { error=-1; } 
 
}

/*
 проверить можно
// start code
             boolean bb = true;
 	     as._IN = 0;
	     as.TIB=s;
	     
             while (bb) {
	           as.ST.push(0);
		   as.WORD();
		   String ss = as.StrBuffer;
              //     String ss =as.word_(s);
                   as.ST.pop();
		   if (! ss.equals("") ) {
		      as.ST.push(0);
		      as.number();
		      if (as.error == -1) { as.error =0;
                                          System.out.println(ss);
		          }
		        else System.out.println("add to stack");
                  }
		  else bb=false; 
	      }
/// end code

*/

//////////////////////////////////////////////////////////  vectors
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



////////////////////////////////////////////////////////////////////////////////////

 public short[] memory = new short[2000];  //выделяем память из 16 битных целых размером в 2000 
public   static int here;  // адрес переменной HERE в массиве memory
public   static int latest; // адрес переменной LATEST в массиве memory
public   static int state;




 public  void immediate() {
   
   short l = memory[latest];
   short i = memory[l+1];
   if (i>0) i = (short)(0-i);
   memory[l+1]=i;
 }
 
 
public void comma16() {  // эквивалентно запятая "," только снимает 16 bit
  short i = (short)ST.pop();
  int adr = memory[here];
  memory[adr]=i; 
  memory[here]++;
}


/* 
Создает в памяти


*/

 public  void cre0(String s) {   // временная вспомогательная ф-ция. создающая упрощенную запись в словаре 
   String STR = s. toUpperCase(); // реализовать проверку на ""  // 
   short tmp = memory[here]; //получить here   
   ST.push(memory[latest] );  
   comma16();                    //записывает указатель на предыдущую статью 
   memory[latest]=tmp;          // latest присваевается указатель на эту статью 
   ST.push( s.length() );  
   comma16();                 // записывается длинна строки 
   ST.push( appendData(StringVector, STR) ); //appendData возвращает индекс на строку
   comma16();              // записывается  индекс на строку
   ST.push(0);
   comma16();          // в cfa записывается 0
 }

 public  void cre1() { // (String s) {   // временная вспомогательная ф-ция. создающая упрощенную запись в словаре 
  ST.push(666);  // код бланк для word - пока не используется
  WORD();
  ST.pop();            // сброс стека, world пока возвращает только 0
  cre0(StrBuffer);
 }

public void setCFA() {
  int i = ST.pop();     // снять со стека нужное значение cfa 
  memory[ memory[latest] + 3] = (short)i;    
  if (i < 0) System.out.println("cfa  -- "+i); 
}

/*
ИСКАТЬ СЛОВО Т В ТЕКУЩЕМ КОНТЕКСТЕ ЕСЛИ N=0, ТО А=Т И СЛОВО НЕ НАЙДЕНО, ИНАЧЕ A=CFA НАЙДЕННОЙ СТАТЬИ, N=1 ДЛЯ СЛОВ "IMMEDIATE" И N=-1 ДЛЯ ОСТАЛЬНЫХ
*/
 
public void FIND() { //поле кода   n 0 - не найдено  если не отрицательно то immediate 
 int ret=0;
 boolean bool=false;
 ST.pop(); // сброс стека, ----> 0 тут должен анализ ecли  0 то  s=StrBuffer иначе из вектора
 String s=StrBuffer. toUpperCase();

  boolean immediate=false;
 int tmp=memory[latest];
 while (tmp != 0 && ! bool) {
   immediate=false;  
   int len = memory[tmp+1];
   if (len<0) {immediate=true;} // если длинна слова отрицательна  - immediate=true; 
   int ind = memory[tmp+2]; // получаем инд в векторе  	     
   String si = (String)StringVector.elementAt(ind);
   if ( s.compareTo(si)==0) {
      bool = true;    
      ret=tmp+3;// memory[tmp+3]; //cfa
      }                     
    else
    tmp=memory[tmp]; 
 
 }//while  
 
 ST.push(ret);
 if (ret==0) ST.push(0);
         else 
	 if (   ! immediate) ST.push(-1);  
	   else {ST.push(1); System.out.println("immediate!!");}
 
}  


// link strsize nomberVector 

/*  протестировать можно     
// start code
             boolean bb = true;
 	     as._IN = 0;
	     as.TIB=s;
	     
             while (bb) {
	           as.ST.push(0);
		   as.WORD();
		   String ss = as.StrBuffer;
              //     String ss =as.word_(s);
                   as.ST.pop();
                   
		   if (! ss.equals("") ) {
	         	      as.ST.push(0);

                              as.FIND();
		             int i =  as.ST.pop();
		             if (i != 0) {
		                    System.out.println(ss+" - найдено");
			            as.ST.pop(); //сбросить признак immediate   // сбросить адрес ?
			            }
	 		           else {   as.ST.pop();// System.out.println(ss+" - ненайдено");  
		                        as.ST.push(0); 
					as.number();			
			                if (as.error != -1)  {System.out.println(ss+" add to stack"); } 
	         		            else { as.error = 0; 
		           	                 as.cre0(ss);
                                                 System.out.println(ss+ " add to vocabulary");  
                                                }
                      //             as.ST.pop(); //сбросить признак immediate
                                }//end else
                  }
		  else bb=false; 
	      }//endwhile
/// end code
*/


////////////////////////////////////////////////////////////////////////////////////FVM////////////////////////////////

 
/* Part of the project take  from retro forth
http://forthworks.com/retro/
*/


public class FVM {
 
 
        public int IMAGE_SIZE=2024;  //my   this is param for imgsize

        private int sp = 0, rsp = 0, ip = 0; // указатели стека, стека возвратов и адрес команды
 
        public STACK stack,adrStack;    //стеки  

        public short[] image=null;  // память

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
   
    VM_LIT32 = 31 ,// положить 32 битное значение на стек
    VM_FETCH16 = 32 ,  VM_STORE16 = 33   // c@  c!   
     ;

  private void handleDevices() {  // от ретро, // вызывается командой wait
        //зависит от реализации
  }

 private void callService(int port, int y) { // вызывается во время записи в порт (out)
        // зависит от реализации
   
   if (port != 0) System.out.println("нетот порт "+ port);
     else
     {
     switch (y) {
       case 0 : _HALT=true; break;
       case 1 : System.out.println("st= "+stack.pop() ); /*_HALT=true; */ break;
       case 2 :  proc(); /* _HALT=true; */  break;
       case 3 :  ret(); /* _HALT=true; */ System.out.println("ret");  break;
       case 4 :  cre1(); System.out.println("fvm create ");  break;
       case 5 :  allot(); break;
       case 6 :   does_(); break; 
       case 7 :   comma();     break;  
       case 8 :   here() ;    break;      
       case 9 :   compile();     break;  
       case 10 :   immediate();     break;  
       case 11 :   resolve_();     break;  
       case 12 : _resolve();     break; 
 //      case 13 :   as.loadScr();     break; 
        case 14 :   comment();     break; 
 //      case 15 :   as._loop_();      break; 
 //      case 16 :   as.compile2();     break;                
        case 17 :   ascii();     break;
        case 18 :  qstring();     break; 
        case 19 :  stype();     break; 
        case 20 :  qtype();     break; 
        case 21 :  sconcat();     break; 
//      case 6 :   as.     break; 
//      case 6 :   as.     break; 
//      case 6 :   as.     break;               
             //as.colon1(); _HALT=true;  break;           
      } //switch
   }//else
 }
 
  /**    * Process a single opcode*/ 
// 
//  public int magicValue = 200; //  если опкод превысит это значение  то или call или jump туда


  private void process() {

  int x, y, z,tmp,  op;
  op = image[ip];
        switch(op) {

    case VM_NOP:
      break;
    case VM_LIT:    ip++;stack.push(image[ip]); // положить 16 битное значение на стек
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


    case VM_CALL:  ip++; adrStack.push(ip); ip=image[ip]-1;
      break;
    case VM_JUMP:  ip++;    ip = image[ip]-1;
      break;
    case VM_RETURN:
      ip = adrStack.pop();
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
    case VM_EQ_JUMP:  ip++;
     x = stack.pop(); if (x==0) ip = image[ip] - 1;     
      break;
   
   
    case VM_FETCH: /// @
      tmp=stack.pop();                        // со стека снимается адрес
      x=image[tmp]; tmp++; y=image[tmp];      // с этого адреса снимаются две соседние
      stack.push(x << 16  | y & 0xffff  );  // 16битные ячейки и с помощью сдвига и AND
      break;              // объединяются в 32 битное значение и кладутся на стек


    case VM_STORE:  //    //  обратная операция 32 битное знач. разлагается
    x = stack.pop(); y= stack.pop(); // на два 16 битных с помощью сдвигов 
    short a=(short)y ,  b = (short) (y >> 16);
    image[x]=b; x++ ;  image[x]=a;
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
    case VM_ZERO_EXIT:    // выход из подпрограммы, если на стеке 0
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

    case VM_LIT32:  // положить 32 битное значение
      ip++; x=image[ip]; ip++; y=image[ip];  stack.push( x << 16  | y & 0xffff );
      break; //2 16 битные ячейки объединяются в 32 битную с помощью сдвигов

    case VM_FETCH16: /// c@
      tmp=stack.pop();                        // со стека снимается адрес
      x=image[tmp];  
      stack.push(x ); 
      break;          

    case VM_STORE16:  // c!   //  обратная операция 32 битное знач. разлагается
    x = stack.pop(); y= stack.pop(); // на два 16 битных с помощью сдвигов 
   // short 
     b = (short) (y);
    image[x]=b; 
      break;

  }
}


  public boolean _HALT =  false;  //Переменной Halt присваиваются значение во время выполнения
                                //callService, при исполнении инструкции out

 public void processImage( int startIP ) {
  ip=startIP;
  _HALT=false;
  while  (ip<IMAGE_SIZE  && ! _HALT) {
      process(); 
      ip++;}
  } //void
  
   public void processImageQ( int startIP ) {ip=startIP;  process();}



 public String testgets(Object o, String f) {
  String  ret = null;
  try {
       Field field = o.getClass().getDeclaredField(f);
       field.setAccessible(true);
       String  name = (String) field.get(o);
       ret = name;
   } catch (NoSuchFieldException | IllegalAccessException e) {
       e.printStackTrace();
       
   }
 return ret;
 }

 public String testget(Object o) {
  /*
  try {
       Field field = fas.getClass().getDeclaredField("ref");
       field.setAccessible(true);
       String  name = (String) field.get(fas);
   } catch (NoSuchFieldException | IllegalAccessException e) {
       e.printStackTrace();
   }
  */
  Class<fas> carClass = fas.class;
Field[] declaredFields = carClass.getDeclaredFields();
for (Field field :declaredFields) {
    System.out.println(field);

}
Field fis;
Field fii;
try {
 fis = carClass.getDeclaredField("ref") ;  
/// fis.setAccessible(true);
 String stri = (String) fis.get(o);  
  System.out.println(fis + "----s "+stri);
 fii = carClass.getDeclaredField("refi");   int ii = fii.getInt(o);  System.out.println(fii + "----i "+ii);

  fis.set(o, "newwal" );
  System.out.println(ref);
  fii.setInt(o,666);
    System.out.println(refi+ "new int");
} catch (NoSuchFieldException | IllegalAccessException e) {
    e.printStackTrace();
    }

// String stri = (String) fis.get(carClass);
/// int   ii =  serialNumberField.getInt(fii);
  return null;
  
  }//testget

 }  //

/////fvm
 public String ref = "ginger";
 public int refi = 3;
 

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
 "    LIT32  31 -1  c@ 32 -1 c! 33 -1 "  + // endpart -111 -1 "   +
 " EXIT . " + 
 //  lit 2  lit 0 out ;  notimm        ret immediate
 " :  1  2  1   0 29  9    n        ; 1  3  1  0  29  9  i    create  1 4  1 0 29  9  n " +

 " allot 1  5  1  0 29 9  n    does> 1  6  1  0 29 9  n    , 1  7  1  0 29 9  n    here 1  8  1  0 29 9 n " +
 
 " compile 1  9  1  0 29 9  n   immediate 1  10  1  0 29 9  n    >resolve 1  11  1  0 29 9  n   <resolve 1  12  1  0 29 9  n " +

 " load 1  13  1  0 29 9  n    ( 1  14  1  0 29 9  i  (loop) 1  15  1  0 29 9  n   compile2 1 16  1  0 29 9  n "   + 
 " ascii 1 17  1  0 29 9  n    s\" 1 18 1 0 29 9 i    type 1 19  1 0 29 9  n  .\" 1 20 1 0 29 9 i  s+  1 21 1 0 29 9 n " +
 " end_all 9 n " +
 " : <   - 0< ; \n  : =   - 0= ;  \n  : >  - 0> ;  \n \n \n " +
 ": >mark here 1 allot ;  \n  : <mark here ; \n \n \n " + 

 " : if compile ?branch  >mark ; immediate \n" +
 " : then >resolve ; immediate \n " +
 " : else   compile branch  >mark swap >resolve ; immediate \n \n " +

 " : begin <mark ; immediate \n" + 
 " : until compile ?branch <resolve ; immediate \n " +
 " : while  compile ?branch  >mark   ; immediate \n " +
 " : repeat  compile branch  swap <resolve  >resolve ; immediate \n \n \n" +

 " : var   create 2 allot   ;  \n  : const create , does> @ ; \n\n " +
 
 " var tmpregistr0  var tmpregistr1  var tmpregistr2  \n\n " +
 
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
 " : 2drop  drop drop ; \n" 

 ;   

 public void initFVMwords() {
   TIB = null; TIB = initwords;
   _IN=0;
  boolean enddo=false; //для выхода из цикла
  do {
      ST.push(666);  WORD();  // на стеке 0    
      ST.pop();  // сброс стека
      String  name  = StrBuffer;
      cre0(name);
      ST.push(666); WORD(); // opcode
      ST.pop();  // сброс стека
      int code=0;
      try { code=Integer.parseInt(StrBuffer);} 
          catch (Exception e){}
      ST.push(code); comma16();  // компилипуем opcode
      ST.push(666); WORD(); // immediate
      ST.pop();  // сброс стека         пропускаем immediate

      ST.push(1);  setCFA();   
      ST.push(9); comma16();  // 9 - код операции ";" возврат из подпрограммы 
      if (name.compareTo("c!") == 0)
                            enddo=true;
      } while (! enddo) ;		    
  }
 
/// lit  2  lit 0 out ;   1 2 1 0 29 9
 public void initEXTwords() {           //  перенести вниз перед comma
    boolean enddo=false; //для выхода из цикла
    boolean endparam=false; //для выхода из цикла    
    do {
      ST.push(666);  WORD();  // на стеке 0    
      ST.pop();  // сброс стека
      String  name  = StrBuffer;
      cre0(name);

      do {
          endparam=false; //для выхода из цикла    
          ST.push(666);  WORD();  // на стеке 0    
          ST.pop();  // сброс стека
          String  s  = StrBuffer;          
          int code=0;
          try { code=Integer.parseInt(s);
                ST.push(code); comma16();  // компилипуем opcode	       
	     } 
             catch (Exception e){
	            if (s.equals("i") ) 
		                        immediate();
                    ST.push(2);  setCFA();      // cfa = 2   		     
                    endparam=true;
		    }	  
	  //param
     
	 } while (! endparam) ;
      if (name.compareTo("end_all") == 0)
                            enddo=true;
   } while (! enddo) ;		
 }


public void exec () {
  int addr=ST.pop();  // System.out.println("code "+memory[addr+1]); 
  int ad = memory[addr]; 
  if (ad == 1 || ad == 2 ) {    // если cfa 1 2
    VM.adrStack.push(exit_addr-1);  // положить на стек возвратов адрес процедуры exit
    VM.processImage(addr+1 );
    }
    else  

      if (ad == 0) ST.push(addr+1);  //  если 0 положить adr pfa
      
       else 
         if (ad < 0 )  {  
	//  System.out.println("cfa=" + ad);
	   ST.push(addr+1);  //  положить adr pfa
	    VM.adrStack.push(exit_addr-1);
	   VM.processImage( -ad + 1) ; //addr+1 );
	   } 


}//void

// добавить в init 
/*

      fas.STACK RST =  as. new STACK();     // создается стек возвратов
      int[] ret_stackarray=new int[30];
      RST.stack=ret_stackarray;//new int[100]; //stackarray;
   //   as.STa=RST;

      fas.FVM VM = as. new FVM();  // создается виртуальная машина
      VM.stack=as.ST;
      VM.adrStack=RST;
      VM.image=as.memory;
      int[] ports = new int[20]; 
      VM.ports=ports; 
      as.VM=VM;   

///


  as.initFVMwords();    // создаются словари для опкодов вирт. машины 

 
 as.cre1();  // создается  определение для exit
 
 as.exit_addr=as.memory[as.here];//сохранить точску входа в exit,чтобы положить на стек возвратов  
 
 as.ST.push(1); as.comma16();  // lit  
 as.ST.push(0); as.comma16();  // 0
 as.ST.push(1); as.comma16();  // lit  
 as.ST.push(0); as.comma16();  // 0 
 as.ST.push(29); as.comma16(); // out      -- вывод в порт 0, номер фции 0 "exit"

  as.ST.push(2);  as.setCFA();


// определяется "." 
 as.cre1();
  as.ST.push(1); as.comma16();  // lit  
 as.ST.push(1); as.comma16();  // 1     
 as.ST.push(1); as.comma16();  // lit  
 as.ST.push(0); as.comma16();  // 0  // номер порта
 as.ST.push(29); as.comma16(); // out      -- вывод в порт 0, номер фции 1 "." 
  as.ST.push(9); as.comma16();  //9  ";"

  as.ST.push(2);  as.setCFA();

*/

/*   заменить code на 
// start code
             boolean bb = true;
 	     as._IN = 0;
	     as.TIB=s;
	     
             while (bb) {  // цикл разделения строк на слова
	           as.ST.push(0);
		   as.WORD();
		   String ss = as.StrBuffer;
              //     String ss =as.word_(s);
                 //  as.ST.pop();
                   
		   if (! ss.equals("") ) {
	         	    //  as.ST.push(0);

                              as.FIND(); 
		             int i =  as.ST.pop();// снять со стека признак немедленного исполнения
		             if (i != 0) {
		                    System.out.println(ss+" - найдено");
			      //      as.ST.pop(); //сбросить признак immediate
				    
				    as.exec();
				//          as.ST.pop(); //сбросить признак immediate
			            }
	 		           else { //  as.ST.pop();// System.out.println(ss+" - ненайдено");  //сбросить признак immediate 
		                       // as.ST.push(0); 
					as.number();			
			                if (as.error != -1)  {System.out.println(ss+" add to stack"); } 
	         		            else { as.error = 0; 
		           	              //   as.cre0(ss);
                                                 System.out.println(ss+ " add to vocabulary");  
                                                }
                      //             as.ST.pop(); //сбросить признак immediate
                                }//end else
                  }
		  else { bb=false; as.ST.pop();}
	      }//endwhile
/// end code

*/

 public void comp  () {
   int addr=ST.pop();

  if ( memory[addr] == 2 ) { // если cfa = 2
              ST.push(7);      comma16();  // код команды call  
	      ST.push(addr+1); comma16();
	       }   
	       else  
	       if  (  memory[addr] == 1 ) {     
                      memory [ memory[here] ] =memory[addr+1];/// !!!  _body
                   //    System.out.println("here="+memory[here]+" addr="+addr+" val="+memory[addr+1]);
                       memory[here]++; 
		       }
		       else 
		       

		       if  (  memory[addr] < 0 )  {
		         ST.push(1);      comma16();  // код команды lit  
		      	ST.push( addr+1 ); comma16();		       
		       
                          ST.push(7);      comma16();  // код команды call  
	                  int i = -memory[addr]+1;///
			  ST.push(i); comma16();		             
			//   System.out.println(" compile " + i);
			   
			    }
			   else
			    if  (  memory[addr] == 0 )  {
			        ST.push(1);      comma16();  // код команды lit  
	                        ST.push( addr+1 ); comma16();
			       }
 }


 public void literal() { 
  if (memory[state]==1) {
   ST.push( FVM.VM_LIT) ;   comma16(); // compile lit
   comma16();                       // compile ","
   }
 }    

public void proc() {

   // добавить тестирование пустого слова ""
 //  System.out.println(" create " +str);
   cre1();
   ST.push(2) ; //   установить cfa = 2
   setCFA();
   memory[state]=1; // перевод в режим компиляции
} 


public void ret()  { 
  ST.push( VM.VM_RETURN ); //вкомпилировать return
  comma16();  
  memory[state]=0 ;
}

public void here() {
  ST.push( memory[here] );
}

public void does_() {
// ST.push( VM.VM_RETURN );  // вкомпилируем ";" 
// comma16();  
 int i = VM.adrStack.pop(); // peek();  // сохранить  со стека возвратов значение, которое занесло туда вызов does 
 ST.push ( -i + 1 ); 
 setCFA();  // устанавливает отрицательный адрес cfa   
 VM._HALT=true;   // остановить VM

VM.adrStack.pop(); //?????????????????????????????????  очистить стек возвратов
}


public void compile() { 
   int i = VM.adrStack.pop(); //peek();  // сохранить  со стека возвратов значение, которое занесло туда вызовc ompile()
System.out.println("compile " +memory[ i+1] );
   ST.push( memory[ i+1] );
   comma16();
VM.adrStack.push(i+1);  // обойти следущую после compile инструкцию 
}

public void resolve_() {
   int i = ST.pop();  // 
   here(); 
 //  int m = ST.pop();
   memory[i] = (short)ST.pop();
//   ST.push(i);
//   comma16();
}
public void _resolve() {
  comma16();
} 


public void allot() {
  memory[here]+=ST.pop(); 
} 

public void comment() {
  boolean log = true;
  while (log) {
    ST.push(666); // разделитель для word  
    WORD(); 
   ST.pop();  // сброс word     
    if ( StrBuffer.compareTo( ")" )==0  ) log = false; 
  }//while
}

public void comma() {
    int adr = memory[here] ; 
    int y= ST.pop(); // на два 16 битных с помощью сдвигов 
    short a=(short)y ,  b = (short) (y >> 16);
    memory[adr] =b ;   memory[adr+1]=a;
     ST.push(2); allot(); 

}


public  void ascii() {  ST.push(32); WORD();
                        ST.pop(); 
			ST.push( (int)StrBuffer.charAt(0) ) ;
}

 public void qstring() {  // s"  на верш длинна 
   int N = TIB.indexOf('\"' ,  _IN);
   String s = TIB.substring(_IN,N);
   ST.push( appendData(StringVector, s )  ); //appendData возвращает индекс на строку
   ST.push( s.length() );
   _IN = N+1;    
 }

 public void stype() {  // type
   ST.pop(); //сбросить длинну
   int ind = ST.pop(); 
   System.out.println( getStringv(StringVector, ind) );
 }

 public void qtype() { // ."
   int N = TIB.indexOf('\"' ,  _IN);
   String s = TIB.substring(_IN,N);

   if (  memory[state]==0 ) { // режим исполнения
      System.out.println(s);
      _IN = N+1;  
      }
      else {  
        ST.push( appendData(StringVector, s )  ); //appendData возвращает индекс на строку
        literal();
        ST.push( s.length() );
        literal();
	stype(); //??????/
       _IN = N+1; 
       }
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

/*
 // start code
             boolean bb = true;
 	     as._IN = 0;
	     as.TIB=s;
	     
             while (bb) {  // цикл разделения строк на слова
	           as.ST.push(0);
		   as.WORD();
		   String ss = as.StrBuffer;
              //     String ss =as.word_(s);
                 //  as.ST.pop();
                   
		   if (! ss.equals("") ) {
	         	    //  as.ST.push(0);

                              as.FIND(); 
		             int i =  as.ST.pop();// снять со стека признак немедленного исполнения
		             if (i != 0) {
		                    System.out.println(ss+" - найдено");
				    if (as.memory[as.state]==0 || i==1 ) 
				      as.exec();
				      else as.comp();
			            }
	 		           else { 
					as.number();			
			                if (as.error != -1)  {System.out.println(ss+" add to stack");  as.literal() ; } 
	         		            else { as.error = 0; 
		           	              //   as.cre0(ss);
                                                 System.out.println(ss+ " notfound");  
                                                }
                                }//end else
                  }
		  else { bb=false; as.ST.pop();}
	      }//endwhile
/// end code
*/

 public void interpret() {

  boolean log = true;     

  while ( log ) { 
    String wrl; 
    ST.push(666); // разделитель для word  
    WORD();    // на стеке 0
    wrl = StrBuffer;  //  
    if (wrl=="") { 
                 log = false;  //  достигнут конец потока 
                 ST.pop();  // сбросить стек после word 
		}
              else { 
	          FIND();                // на стеке n  и 
                  int n = ST.pop();     // снять со стека признак немедленного исполнения
        	  if (n==0) {         //слово не найдено - проверить если число
		           number(); //забирает со стека <адр строки> -  пока  0
		           if (error != 0)  {   // если число - number оставляет 0
			                   log = false;
			  		   System.out.println("слово " +  wrl + " не найдено" );
					   error = 0;
					   } else literal() ; 
					   //System.out.println("число " +  wrl + " на стеке" );
 			      }
		    else  
                      if (memory[state]==0 || n==1 ) // если find вернул immediate или состояние исполнения
			     exec();
			      else 
			      comp();
	      }//elsefind
  }// while
 }//interpret

} 
//////////////////////////end all////////////////////////////////////
