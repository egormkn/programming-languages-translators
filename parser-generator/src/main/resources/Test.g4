grammar Test;

e: c ePrime;

ePrime: Bar c ePrime | ;

c: k cPrime;

cPrime: k cPrime | ;

k: t KPrime;

KPrime: [+*]?;

t: Lparen e Rparen | Letter;

Bar: '|';
Lparen: '(';
Rparen: ')';

Letter: [a-z];