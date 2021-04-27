# Forth
Building forth for scripts in program

 NOP 
 LIT 
 dup     drop     swap 
   >R   R>  
     CALL     branch   ;  
     0>     0<     0=      ?branch  
     @   !                                         
    +    -     *    /MOD        
   AND    OR     XOR    SHL     SHR  
    ZERO_EXIT       1+     1-  
      IN    OUT       WAIT      +
   LIT32     c@  c! 3 
   
  EXIT .  
  
   :        ;    create   

 allot     does>    ,      here  
 
   compile    immediate     >resolve    <resolve  

      (  
  ascii     s"    type   ."  s+  
  
    0<   0=    0> 
 ": >mark  <mark  

   if  
  then 
  else   

  begin  
  until  
  while   
   repeat   

   var    
 
 
 
 over  
   rot  
   ?DUP  
  R@  

   /     /mod  
  
  2+  
  2-   2/  
 negate  
  ABS ( A --->абс A )  

 2dup     
  2drop   

