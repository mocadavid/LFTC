%{
#include <stdio.h>
#include <stdlib.h>

#define YYDEBUG 1
%}

%token IDENTIFIER
%token CONST
%token PROGRAM
%token SEMI_COLON
%token ENDPROGRAM
%token CHAR
%token BOOL
%token NUM
%token STRING
%token LEFT_SQUARE_PARENTHESIS
%token RIGHT_SQUARE_PARENTHESIS
%token LEFT_ROUND_PARENTHESIS
%token RIGHT_ROUND_PARENTHESIS
%token EQUAL
%token PLUS
%token MINUS
%token MULTIPLY
%token DIVISION
%token MOD
%token IF
%token ELSE
%token ENDIF
%token ENDELSE
%token READ
%token WRITE
%token WHILE
%token ENDWHILE
%token LESS_THAN
%token GREATER_THAN
%token IS
%token AND
%token OR
%token NOT
%token DIFFERENT
%token LESS_OR_EQUAL_THAN
%token GREATER_OR_EQUAL_THAN


%start programDef

%%


programDef : PROGRAM IDENTIFIER SEMI_COLON stmtlist ENDPROGRAM SEMI_COLON ;
stmtlist : stmt | stmt stmtlist ;
stmt : simplestmt | structstmt | declar ;
simplestmt : assignstmt | iostmt ;
declar : type IDENTIFIER SEMI_COLON ;
type : type1 | type2 ;
type1 : BOOL | CHAR | NUM | STRING ;
type2 : type1 LEFT_SQUARE_PARENTHESIS CONST RIGHT_SQUARE_PARENTHESIS ;
assignstmt : IDENTIFIER EQUAL expression | IDENTIFIER EQUAL accesvector | accesvector EQUAL expression | accesvector EQUAL accesvector ;
accesvector : IDENTIFIER LEFT_SQUARE_PARENTHESIS CONST RIGHT_SQUARE_PARENTHESIS | IDENTIFIER LEFT_SQUARE_PARENTHESIS IDENTIFIER RIGHT_SQUARE_PARENTHESIS ;
operators : PLUS | MINUS | DIVISION | MULTIPLY | MOD ;
expression : CONST operators IDENTIFIER | CONST operators CONST | IDENTIFIER operators IDENTIFIER | IDENTIFIER operators CONST | accesvector operators IDENTIFIER | accesvector operators CONST ;
iostmt : READ LEFT_ROUND_PARENTHESIS IDENTIFIER RIGHT_ROUND_PARENTHESIS SEMI_COLON | WRITE LEFT_ROUND_PARENTHESIS IDENTIFIER RIGHT_ROUND_PARENTHESIS SEMI_COLON ;
structstmt : ifstmt | whilestmt ;
ifstmt : IF LEFT_ROUND_PARENTHESIS condition RIGHT_ROUND_PARENTHESIS stmtlist ELSE stmtlist ENDELSE ENDIF | IF LEFT_ROUND_PARENTHESIS condition RIGHT_ROUND_PARENTHESIS stmtlist ENDIF ;
whilestmt : WHILE LEFT_ROUND_PARENTHESIS condition RIGHT_ROUND_PARENTHESIS stmtlist ENDWHILE ;
condition : cond1 | cond2 ;
cond1 : IDENTIFIER aritoperators IDENTIFIER | IDENTIFIER aritoperators CONST | IDENTIFIER aritoperators accesvector | CONST aritoperators IDENTIFIER | CONST aritoperators CONST | CONST aritoperators accesvector | accesvector aritoperators IDENTIFIER | accesvector aritoperators CONST | accesvector aritoperators accesvector ;
cond2 : IDENTIFIER reloperators IDENTIFIER | IDENTIFIER reloperators CONST | IDENTIFIER reloperators accesvector | CONST reloperators IDENTIFIER | CONST reloperators CONST | CONST reloperators accesvector | accesvector reloperators IDENTIFIER | accesvector reloperators CONST | accesvector reloperators accesvector ;
aritoperators : LESS_THAN | LESS_OR_EQUAL_THAN | IS | DIFFERENT | GREATER_OR_EQUAL_THAN | GREATER_THAN ;
reloperators : AND | OR | NOT ;

%%

yyerror(char *s)
{
  printf("%s\n", s);
}

extern FILE *yyin;

main(int argc, char **argv)
{
  if (argc > 1) 
    yyin = fopen(argv[1], "r");
  if ( (argc > 2) && ( !strcmp(argv[2], "-d") ) ) 
    yydebug = 1;
  if ( !yyparse() ) 
    fprintf(stderr,"\t Working !\n");
}
