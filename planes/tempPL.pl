% Permite ver si se puede o no usar la accion en base a sus precondiciones y el estado actual del plan.

accion(mover,E):- member(pitoanalia,E).

% Agrega sus postcondiciones en el estado actual del plan.

aplicar(mover,EA,ES):- insertar(chaconjuan,EA,ES).

% Predicados auxiliares y el predicado pricipal planear.

pertenece([],_).
pertenece([C|L],L2):- member(C,L2), pertenece(L,L2).
del(H,[H|T],T).
del(H,[Y|T],[Y|T1]):- del(H,T,T1).
insertar(X,L,L1):- del(X,L1,L).
inv([],[]).
inv([H|T],L):- inv(T,Z), append(Z,[H],L).
planear(EI,EF,_,_,[]):- pertenece(EF,EI).
planear(_,_,_,0,_):- !,fail.
planear(_,_,[],_,[]):- !,fail.
planear(EI,EF,[A|LA],CONT,[A|SOL]):- accion(A,EI), aplicar(A,EI,EN), planear(EN,EF,LA,CONT,SOL).
planear(EI,EF,LA,CONT,SOL):- !, inv(LA,L), CONT1 is CONT - 1, planear(EI,EF,L,CONT1,SOL).
