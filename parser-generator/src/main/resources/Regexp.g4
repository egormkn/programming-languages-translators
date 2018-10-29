grammar Regexp;

@header {
package ru.ifmo.translators.generated;
}

e: c ePrime;

ePrime: '|' c ePrime | ;

c: k cPrime;

cPrime: k cPrime | ;

k: t KPrime?;

KPrime: [+*];

t: '(' e ')' | Letter;

Bar: '|';
Lparen: '(';
Rparen: ')';

Letter: [a-z];
