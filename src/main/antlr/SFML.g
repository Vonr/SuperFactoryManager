grammar SFML;

program : name trigger*;

name: NAME string ;

trigger     : EVERY interval DO block END           #TimerTrigger
            | EVERY REDSTONE PULSE DO block END     #PulseTrigger
            ;

interval  : number TICKS     #Ticks
          | number SECONDS   #Seconds
          ;

block: statement* ;

statement   : INPUT FROM label  #InputStatement
            | OUTPUT TO label   #OutputStatement
            ;

string: STRING ;
number: NUMBER ;
label: IDENTIFIER ;


MOVE    : M O V E ;
FROM    : F R O M ;
TO      : T O ;
INPUT   : I N P U T ;
OUTPUT  : O U T P U T ;


TICKS   : T I C K S ;
SECONDS : S E C O N D S ;

EVERY       : E V E R Y ;
REDSTONE    : R E D S T O N E ;
PULSE       : P U L S E;

DO      : D O ;
WORLD   : W O R L D ;
PROGRAM : P R O G R A M ;
END     : E N D ;
NAME    : N A M E ;

IDENTIFIER      : [a-zA-Z_][a-zA-Z0-9_]+ ;
NUMBER          : [0-9]+ ;

STRING : '"' (~'"'|'\\"')* '"' ;

LINE_COMMENT : '--' ~[\r\n]* (EOF|'\r'? '\n') -> skip ;

WS
        :   [ \r\t\n]+ -> channel(HIDDEN)
        ;

fragment A  :('a' | 'A') ;
fragment B  :('b' | 'B') ;
fragment C  :('c' | 'C') ;
fragment D  :('d' | 'D') ;
fragment E  :('e' | 'E') ;
fragment F  :('f' | 'F') ;
fragment G  :('g' | 'G') ;
fragment H  :('h' | 'H') ;
fragment I  :('i' | 'I') ;
fragment J  :('j' | 'J') ;
fragment K  :('k' | 'K') ;
fragment L  :('l' | 'L') ;
fragment M  :('m' | 'M') ;
fragment N  :('n' | 'N') ;
fragment O  :('o' | 'O') ;
fragment P  :('p' | 'P') ;
fragment Q  :('q' | 'Q') ;
fragment R  :('r' | 'R') ;
fragment S  :('s' | 'S') ;
fragment T  :('t' | 'T') ;
fragment U  :('u' | 'U') ;
fragment V  :('v' | 'V') ;
fragment W  :('w' | 'W') ;
fragment X  :('x' | 'X') ;
fragment Y  :('y' | 'Y') ;
fragment Z  :('z' | 'Z') ;