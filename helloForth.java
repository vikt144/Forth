/*

*/

import java.util.*;
import forth.*;

public class helloForth  {

 /*  


   public class fas {

public volatile int BLK_ = 0 ; // перенести в память
public  int _IN=0;       // !!!!to memory 
public String TIB; //  строковая переменная - иммитирует tib
public String StrBuffer;  //  сюда возвращает значение word и забирает find
 

*/

  //  fas as; //= new fas(); 

    public static void main(String[] args) {

      fas  as = new fas();


      fas.STACK ST =  as. new STACK();
      int[] stackarray=new int[100];
      ST.stack=stackarray;//new int[100]; //stackarray;
      as.ST=ST;

//init
      as.here=  2;
      as.  latest=3;
      as.state   =4;      
      as.  memory[as.here]=6; //взято от балды
      as.  memory[as.latest]=0; // записи в словари еще не создавались
      as.  memory[as.state] =0; // 0 на данный момент исполнение     

     Vector V=null;
     V = as.initVirtualMem(0);
     as.StringVector=V;


//init 

      fas.STACK RST =  as. new STACK();
      int[] ret_stackarray=new int[30];
      RST.stack=ret_stackarray;//new int[100]; //stackarray;
   //   as.STa=RST;

      fas.FVM VM = as. new FVM();
      VM.stack=as.ST;
      VM.adrStack=RST;
      VM.image=as.memory;
      int[] ports = new int[20]; 
      VM.ports=ports; 
      as.VM=VM;   

///

//init
  as.initFVMwords();




/// as.TIB=null; as.TIB="EXIT  . : ; create allot does> , here compile  immediate >resolve <resolve load ( (loop) compile2 ";
///  as._IN=0; // тут дальнейшие определения
 
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

//init 
 as.initEXTwords();
 as.interpret();
   
      Scanner in = new Scanner(System.in);
 
      boolean b = true;

      String s="";

       while (b) {

        System.out.println(" введите строку или end для выхода");

         s=null; 
	 
	 s = in.nextLine();

         if ( ! s.trim() . equals("end") ) {

// start code
             boolean bb = true;
 	     as._IN = 0;
	     as.TIB=s;
  as.interpret();
/*	     
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
           }      
      else b = false;

    }//while	 

   while (as.ST.getDepth() != 0 ) System.out.println(as.ST.pop()+"\n" );  //////////стек
//   for (int k=0;k<V.size();k++) System.out.println(V.elementAt(k).toString() ) ; /// вектор строк

 as.VM.testget(as);
  
   String s1 = as.VM.testgets(as,"ref")  ;
  System.out.println(s1);
   } //main
  static String iii = "hooos";
}///	 
	 
