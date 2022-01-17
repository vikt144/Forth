/*

*/
import java.lang.*;
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
     String filename=null;
      String firstfile=null;
      if ( args != null  &&   args.length > 0  )  {// System.out.println("nj= Это="+args.length );
         filename=args[0]; 
         firstfile=as.loadTextFile(filename); //загрузка файла, если есть   
       }

      fas.STACK ST =  as. new STACK();
      int[] stackarray=new int[100];
      ST.stack=stackarray;//new int[100]; //stackarray;
      as.ST=ST;



//init
  as.mem = as.createMemory(null,100000);
  if (as.mem==null) System.out.println("memo  error"); else System.out.println("memoyes");
   
      as.here    = 2; //  адреса в памяти, где хранятся эти  переменные
      as.latest  =6;
      as.state   =10;      
      as.context =  15;
      as.current =  20;
      as.forthVoc=  25; 

      as.mem.putInt(as. here,45);//  as.  memory[as.here]=6; //взято от балды
      as.mem.putInt(as. latest,0);//      as.  memory[as.latest]=0; // записи в словари еще не создавались
      as.mem.putInt(as. state,0);//     as.  memory[as.state] =0; // 0 на данный момент исполнение     


     Vector V=null;               // хранилище строк
     V = as.initVirtualMem(0);
     as.StringVector=V;

    int currentHere = as.newDict("FORTH",0,(byte)0);


    as.mem.putInt(as. context, currentHere);// as.latest);   // для работы create -- начальное должно указывать на  0
    as.mem.putInt(as. current, currentHere);//as.latest);  // см   void cre0(String s)
    as.mem.putInt(as.forthVoc, currentHere);    
   
//init 

  as.initFVMwords();



      fas.STACK RST =  as. new STACK();
      int[] ret_stackarray=new int[30];
      RST.stack=ret_stackarray;//new int[100]; //stackarray;
   //   as.STa=RST;

      fas.FVM VM = as. new FVM();
      VM.stack=as.ST;
      VM.adrStack=RST;
//      VM.image=as.memory;
      int[] ports = new int[20]; 
      VM.ports=ports; 
      as.VM=VM;   

///

//init

 
 as.cre1();  // создается  определение для exit
 
 as.exit_addr=as.mem.getInt(as.here);// as.memory[as.here];//сохранить точску входа в exit,чтобы положить на стек возвратов  
 as.setout_(0,0,false); 

  as.ST.push(2);  as.setCFA();


// определяется "." 
 as.cre1(); 

 as.setout_(1,0,true);
  as.ST.push(2);  as.setCFA();

//init 
//  as.initEXTwords();

      as.VM.Ob=as;
 as.initEXTwords_2(as);


 System.out.println("as.interpret");//initFVMword then");
 as.interpret();

  double[] FL = new double[30]; 
  as.ST.fstack=FL;

 if (firstfile != null ){  
 	     as._IN = 0;
	     as.TIB=firstfile;
            as.interpret();
            }
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

 
           }      
      else b = false;

    }//while	 

   while (as.ST.getDepth() != 0 ) System.out.println(as.ST.pop()+"\n" );  //////////стек
//   for (int k=0;k<V.size();k++) System.out.println(V.elementAt(k).toString() ) ; /// вектор строк

/*
 as.VM.testget(as);
 
//  as.VM.testget(  Math);
// as.VM.testget(  (Object)System);
  
   String s1 = as.VM.testgets(as,"ref")  ;
  System.out.println(s1);

*/
   } //main

}///	 
	 
