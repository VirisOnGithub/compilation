grammar grammarTCL;		

expr: 	
	'(' expr ')'								# brackets
|	'{' (expr (',' expr)*)? '}'					# tab_initialization	
|	VAR '(' (expr (',' expr)*)? ')'				# call
|	expr '[' expr ']'							# tab_access
|	'-' expr									# opposite
|	NOT expr									# negation
|	expr op=(MUL | DIV | MODULO) expr			# multiplication
| 	expr op=(ADD | SUB) expr					# addition
|	expr op=(SUP | INF | SUPEQ | INFEQ) expr	# comparison
|	expr op=(EQUALS | DIFF) expr	     	  	# equality
|	expr AND expr								# and
|	expr OR expr								# or
|	VAR 	      	   	    					# variable
| 	INT	     									# integer
|	BOOL	      	   	    					# boolean
;

type:
	BASE_TYPE									# base_type
|	type '[' ']'								# tab_type
;

instr:
	type VAR (ASSIGN expr)? SEMICOL				# declaration
|	PRINT '(' VAR ')' SEMICOL 					# print
|   VAR ('[' expr ']')* ASSIGN expr SEMICOL		# assignment
|	'{' instr+ '}'								# block
|	IF '(' expr ')' instr (ELSE instr)?			# if
|	WHILE '(' expr ')' instr					# while
|	FOR '(' instr ',' expr ',' instr ')' instr	# for
|	RETURN expr SEMICOL  	    	      		# return
;

core_fct: 
	'{' instr* RETURN expr SEMICOL '}';

decl_fct:
	type VAR '(' (type VAR (',' type VAR)*)? ')' core_fct;

main:
	decl_fct* 'int main()' core_fct EOF;

PRINT : 'print';
SEMICOL : ';';
NEWLINE : [\r\n\t ]+ -> skip;
INT : '-'?[0-9]+ ;
BOOL : 'true' | 'false';
OR : '||';
AND : '&&';
MUL : '*';
DIV : '/';
MODULO : '%';
ADD : '+';
SUB : '-';
SUPEQ : '>=';
INFEQ : '<=';
SUP : '>';
INF : '<';
EQUALS : '==';
DIFF : '!=';
NOT : '!';
ASSIGN : '=';
BASE_TYPE : 'int' | 'bool' | 'auto'; 
IF : 'if';
ELSE : 'else';
WHILE : 'while';
FOR: 'for';
RETURN : 'return';
VAR : [A-Za-z_][A-Za-z0-9_]*;