
e: c ePrime;

ePrime: Bar c ePrime | '';

c: k cPrime;

cPrime: k cPrime | '';

k: t KPrime;

KPrime: RegExp("[+*]?");

t: Lparen e Rparen | Letter;

Bar: '|';
Lparen: '(';
Rparen: ')';
Letter: RegExp("[a-z]");