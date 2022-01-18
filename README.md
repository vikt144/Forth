# Forth
Building forth for scripts in program

  Новый форт имеет простейшую реализацию словарей, 
операции с плавающей точкой и другие новшества.

Список слов
NOP LIT dup drop swap    >R R>    CALL  branch   ;
    0> 0< 0= ?branch
    @ !
    + - * /MOD
    AND OR XOR SHL SHR
    ZERO_EXIT 1+ 1-
    IN OUT WAIT   LIT8  c@ c! 
  EXIT .

 :   ;   create   allot  does>  ,   here
compile   compile_call   immediate   >resolve   <resolve  >mark  <mark
name>    L>NAME      link>    >body   state@ 
word  find

 (  комментарий - пропускает текстовый блок до ')'  
  \ - комментарий не поддерживается

< > = 
if   then else
begin  until while repeat

var  const  

over  rot ?DUP  R@  /   mod
2+  2-  2/
negate
ABS ( A --->абс A )

2dup  2drop   2var 2@  2!

Тип float

Примеры  чисел с 
1.073741824E9  5.304989477E-315  10f  4.6
 В общем все, что совместимо с типом данных для java double
 здесь числа с точкой не являются числом двойной точности

flit  
fdup fswap fdrop
  f.  f! f@  f+ f-  f*  f/   f> f< f= f<> f>= f<=
 fsin fcos ftan  fasin facos fatan fatan2 
 fln flog  fsqrt fexp fabs floor  d>f f>d

Строки 
ascii  --   ascii "  вернет 34
s"  -размещает строку 
type  
s+  ( s0  l0   s1  l1   -   sn  ln   конкатенация строк  )
spick - дублирование строки
s=  сравнение строк

В файле с именем 0 (ноль), находятся расширения, написанные на форте

constant   variable   c,  2const
>name
voc-link - список словарей
адреса переменных в памяти виртуальной машины форта.
 2  constant here_
 6  constant latest
 10 constant state
 15 constant context
 20 constant current
 25 constant forthVoc

vocabulary   DEFINITIONS  forth
