/*  $Id$

    Part of SWI-Prolog

    Author:        Jan Wielemaker
    E-mail:        J.Wielemaker@uva.nl
    WWW:           http://www.swi-prolog.org
    Copyright (C): 1985-2009, University of Amsterdam

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    As a special exception, if you link this library with other files,
    compiled with a Free Software compiler, to produce an executable, this
    library does not by itself cause the resulting executable to be covered
    by the GNU General Public License. This exception does not however
    invalidate any other reasons why the executable file might be covered by
    the GNU General Public License.
*/

/*
Consult, derivates and basic things.   This  module  is  loaded  by  the
C-written  bootstrap  compiler.

The $:- directive  is  executed  by  the  bootstrap  compiler,  but  not
inserted  in  the  intermediate  code  file.   Used  to print diagnostic
messages and start the Prolog defined compiler for  the  remaining  boot
modules.

If you want  to  debug  this  module,  put  a  '$:-'(trace).   directive
somewhere.   The  tracer will work properly under boot compilation as it
will use the C defined write predicate  to  print  goals  and  does  not
attempt to call the Prolog defined trace interceptor.
*/

'$:-'(format('Loading boot file ...~n', [])).

		/********************************
		*    LOAD INTO MODULE SYSTEM	*
		********************************/

:- '$set_source_module'(_, system).

		/********************************
		*          DIRECTIVES           *
		*********************************/

:- meta_predicate
	dynamic(:),
	multifile(:),
	module_transparent(:),
	discontiguous(:),
	volatile(:),
	thread_local(:),
	noprofile(:),
	'$iso'(:),
	'$hide'(:).

'$set_pattr'(_:X, _) :-
	var(X),
	throw(error(instantiation_error, _)).
'$set_pattr'(_:[], _) :- !.
'$set_pattr'(M:[H|T], Attr) :- !,		% ISO
	'$set_pattr'(M:H, Attr),
	'$set_pattr'(M:T, Attr).
'$set_pattr'(M:(A,B), Attr) :- !,		% ISO and traditional
	'$set_pattr'(M:A, Attr),
	'$set_pattr'(M:B, Attr).
'$set_pattr'(A, Attr) :-
	'$set_predicate_attribute'(A, Attr, 1).

dynamic(Spec)		 :- '$set_pattr'(Spec, (dynamic)).
multifile(Spec)		 :- '$set_pattr'(Spec, (multifile)).
module_transparent(Spec) :- '$set_pattr'(Spec, transparent).
discontiguous(Spec)	 :- '$set_pattr'(Spec, (discontiguous)).
volatile(Spec)		 :- '$set_pattr'(Spec, (volatile)).
thread_local(Spec)	 :- '$set_pattr'(Spec, (thread_local)).
noprofile(Spec)		 :- '$set_pattr'(Spec, (noprofile)).
'$iso'(Spec)		 :- '$set_pattr'(Spec, (iso)).

%%	'$hide'(:PI)
%
%	Predicates protected this way are never visible in the tracer.

'$hide'(Pred) :-
	'$set_predicate_attribute'(Pred, trace, 0).


		/********************************
		*       CALLING, CONTROL        *
		*********************************/

:- noprofile((call/1,
	      catch/3,
	      once/1,
	      ignore/1,
	      call_cleanup/2,
	      call_cleanup/3,
	      setup_call_cleanup/3,
	      setup_call_catcher_cleanup/4,
	      (^)/2)).

:- meta_predicate
	';'(0,0),
	'|'(0,0),
	','(0,0),
	call(0),
	call(1,?),
	call(2,?,?),
	call(3,?,?,?),
	call(4,?,?,?,?),
	call(5,?,?,?,?,?),
	call(6,?,?,?,?,?,?),
	call(7,?,?,?,?,?,?,?),
	^(+,0),
	not(0),
	\+(0),
	'->'(0,0),
	'*->'(0,0),
	once(0),
	ignore(0),
	block(+,0,-),
	catch(0,?,0),
	setup_call_cleanup(0,0,0),
	setup_call_catcher_cleanup(0,0,?,0),
	call_cleanup(0,0),
	call_cleanup(0,?,0).

/* Currently, meta_predicate is only supported upto arity 8
*/

:- module_transparent
	call/9,
	call/10,
	call/11.

:- '$iso'((call/1, (\+)/1, once/1, (;)/2, (,)/2, (->)/2, catch/3)).

%   ->/2, ;/2, |/2 and \+/1 are normally compiled. These predicate catch them
%   in case they are called via the meta-call predicates.

(If ->  Then) :- If, !, Then.
(If *-> Then) :- (If *-> Then ; fail).

(If ->  Then; Else) :- !, (If  -> Then ; Else).
(If *-> Then; Else) :- !, (If *-> Then ; Else).
(A ; B) :- (A ; B).

(If ->  Then| Else) :- !, (If  -> Then ; Else).
(If *-> Then| Else) :- !, (If *-> Then ; Else).
(A | B) :- (A ; B).

','(Goal1, Goal2) :-			% Puzzle for beginners!
	Goal1,
	Goal2.

:- '$iso'((call/2,
	   call/3,
	   call/4)).

call(Goal) :-				% make these available as predicates
	Goal.
call(G, A) :-
	call(G, A).
call(G, A, B) :-
	call(G, A, B).
call(G, A, B, C) :-
	call(G, A, B, C).
call(G, A, B, C, D) :-
	call(G, A, B, C, D).
call(G, A, B, C, D, E) :-
	call(G, A, B, C, D, E).
call(G, A, B, C, D, E, F) :-
	call(G, A, B, C, D, E, F).
call(G, A, B, C, D, E, F, G) :-
	call(G, A, B, C, D, E, F, G).
call(G, A, B, C, D, E, F, G, H) :-
	call(G, A, B, C, D, E, F, G, H).
call(G, A, B, C, D, E, F, G, H, I) :-
	call(G, A, B, C, D, E, F, G, H, I).
call(G, A, B, C, D, E, F, G, H, I, J) :-
	call(G, A, B, C, D, E, F, G, H, I, J).

not(Goal) :-
	\+ Goal.

%	This version of not is compiled as well. For meta-calls only

\+ Goal :-
	\+ Goal.

%	once/1 can normally be replaced by ->/2. For historical reasons
%	only.

once(Goal) :-
	Goal, !.

ignore(Goal) :-
	Goal, !.
ignore(_Goal).

_Var^Goal :-					% setof/3, bagof/3
	Goal.

false :-					% SICStus compatibility
	fail.

%	block/3, !/1, exit/2, fail/1
%	`longjmp' like control-structures.  See manual.  The predicate
%	system:block/3 is used by the VMI's I_CUT_BLOCK and B_EXIT.
%	'$exit' and '$cut' are interpreted by the compiler/decompiler.

block(_Label, Goal, _RVal) :-
	Goal.

!(Label) :-
	'$cut'(Label).				% handled by compiler

exit(Label, RVal) :-
	'$exit'(Label, RVal).			% handled by compiler

fail(Label) :-
	'$cut'(Label),				% handled by compiler
	fail.

%%	catch(:Goal, +Catcher, :Recover)
%
%	ISO compliant exception handling.

catch(_Goal, _Catcher, _Recover) :-
	'$catch'.

%%	setup_call_cleanup(:Setup, :Goal, :Cleanup).
%%	setup_call_catcher_cleanup(:Setup, :Goal, +Catcher, :Cleanup).
%%	call_cleanup(:Goal, :Cleanup)
%%	call_cleanup(:Goal, +Catcher, :Cleanup)
%
%	Call Cleanup once after Goal is finished (deterministic success, failure,
%	exception or cut).  '$call_cleanup' translated to I_CALLCLEANUP.

setup_call_catcher_cleanup(Setup, _Goal, _Catcher, _Cleanup) :-
	'$sig_atomic'(Setup),
	'$call_cleanup'.

setup_call_cleanup(Setup, Goal, Cleanup) :-
	setup_call_catcher_cleanup(Setup, Goal, _Catcher, Cleanup).

call_cleanup(Goal, Cleanup) :-
	setup_call_catcher_cleanup(true, Goal, _Catcher, Cleanup).

call_cleanup(Goal, Catcher, Cleanup) :-
	setup_call_catcher_cleanup(true, Goal, Catcher, Cleanup).


		 /*******************************
		 *	 INITIALIZATION		*
		 *******************************/

:- meta_predicate
	initialization(0, +).

:- dynamic
	'$init_goal'/3.

%%	initialization(:Goal, +When)
%
%	Register Goal to be executed if a saved state is restored. In
%	addition, the goal is executed depending on When:
%
%	    * now
%	    Execute immediately
%	    * after_load
%	    Execute after loading the file in which it appears
%	    * restore
%	    Do not execute immediately, but only when restoring the
%	    state.

initialization(Goal, When) :-
	'$initialization_context'(Ctx),
	(   When == now
	->  Goal,
	    assert('$init_goal'(-, Goal, Ctx))
	;   When == after_load
	->  (   Ctx = File:_Line
	    ->	assert('$init_goal'(File, Goal, Ctx))
	    ;	throw(error(context_error(nodirective,
					  initialization(Goal, after_load)),
			    _))
	    )
	;   When == restore
	->  assert('$init_goal'(-, Goal, Ctx))
	;   (   var(When)
	    ->	throw(error(instantiation_error, _))
	    ;	atom(When)
	    ->	throw(error(domain_error(initialization_type, When), _))
	    ;   throw(error(type_error(atom, When), _))
	    )
	).


'$run_initialization'(File) :-
	(   '$init_goal'(File, Goal, Ctx),
	    (   catch(Goal, E, '$initialization_error'(E, Goal, Ctx))
	    ->  fail
	    ;   '$initialization_failure'(Goal, Ctx),
		fail
	    )
	;   true
	).

'$initialization_context'(Ctx) :-
	(   source_location(File, Line)
	->  Ctx = File:Line
	;   Ctx = (-)
	).

'$initialization_error'(E, Goal, Ctx) :-
	print_message(error, initialization_error(Goal, E, Ctx)).

'$initialization_failure'(Goal, Ctx) :-
	print_message(warning, initialization_failure(Goal, Ctx)).

%%	'$clear_initialization'(+File) is det.
%
%	removes all initialization goals that are registered from File.
%
%	@see Called from startConsult() in pl-proc.c

'$clear_initialization'(File) :-
	retractall('$init_goal'(_, _, File:_)).


		/********************************
		*            MODULES            *
		*********************************/

%	'$prefix_module'(+Module, +Context, +Term, -Prefixed)
%	Tags `Term' with `Module:' if `Module' is not the context module.

'$prefix_module'(Module, Module, Head, Head) :- !.
'$prefix_module'(Module, _, Head, Module:Head).

%%	default_module(+Me, -Super) is nondet.
%
%	Is true if `Super' is `Me' or a super (auto import) module of `Me'.

default_module(Me, Me).
default_module(Me, Super) :-
	import_module(Me, S),
	default_module(S, Super).


		/********************************
		*      TRACE AND EXCEPTIONS     *
		*********************************/

:- user:dynamic((exception/3,
		 prolog_event_hook/1)).
:- user:multifile((exception/3,
		   prolog_event_hook/1)).

%	This function is called from C on undefined predicates.  First
%	allows the user to take care of it using exception/3. Else try
%	to give a DWIM warning. Otherwise fail. C will print an error
%	message.

:- set_prolog_flag(autoload, true).
:- set_prolog_flag(verbose_autoload, false).
:- flag('$autoloading', _, 0).

'$undefined_procedure'(Module, Name, Arity, Action) :-
	'$prefix_module'(Module, user, Name/Arity, Pred),
	user:exception(undefined_predicate, Pred, Action), !.
'$undefined_procedure'(Module, Name, Arity, retry) :-
	current_prolog_flag(autoload, true),
	with_mutex('$load', '$autoload'(Module, Name, Arity)).
'$undefined_procedure'(_, _, _, error).

'$autoload'(Module, Name, Arity) :-
	'$find_library'(Module, Name, Arity, LoadModule, Library),
	functor(Head, Name, Arity),
	flag('$autoloading', Old, Old+1),
	(   current_prolog_flag(verbose_autoload, true)
	->  Level = informational
	;   Level = silent
	),
	print_message(Level, autoload(Module:Name/Arity, Library)),
	flag('$compiling', OldComp, database),
	(   Module == LoadModule
	->  ensure_loaded(Module:Library)
	;   (   '$get_predicate_attribute'(LoadModule:Head, defined, 1)
	    ->	Module:import(LoadModule:Head)
	    ;	use_module(Module:Library, [Name/Arity])
	    )
	),
	flag('$compiling', _, OldComp),
	flag('$autoloading', _, Old),
	'$c_current_predicate'(_, Module:Head).

'$calleventhook'(Term) :-
	(   notrace(user:prolog_event_hook(Term))
	->  true
	;   true
	).

:- '$hide'('$calleventhook'/1).

%	 handle debugger 'w', 'p' and <N> depth options.

'$set_debugger_print_options'(write) :- !,
	set_prolog_flag(debugger_print_options,
			[ quoted(true),
			  attributes(write)
			]).
'$set_debugger_print_options'(print) :- !,
	set_prolog_flag(debugger_print_options,
			[ quoted(true),
			  portray(true),
			  max_depth(10),
			  attributes(portray)
			]).
'$set_debugger_print_options'(Depth) :-
	current_prolog_flag(debugger_print_options, Options0),
	(   '$select'(max_depth(_), Options0, Options)
	->  true
	;   Options = Options0
	),
	set_prolog_flag(debugger_print_options,
			[max_depth(Depth)|Options]).


		/********************************
		*        SYSTEM MESSAGES        *
		*********************************/

%	'$confirm'(Spec)
%
%	Ask the user to confirm a question.  Spec is a term as used for
%	print_message/2.

'$confirm'(Spec) :-
	print_message(query, Spec),
	between(0, 5, _),
	    get_single_char(Answer),
	    (	memberchk(Answer, "yYjJ \n")
	    ->	!,
	        print_message(query, if_tty(yes))
	    ;	memberchk(Answer, "nN")
	    ->	!,
	        print_message(query, if_tty(no)),
		fail
	    ;	print_message(help, query(confirm)),
		fail
	    ).

:- dynamic
	user:portray/1.
:- multifile
	user:portray/1.


		 /*******************************
		 *	 FILE_SEARCH_PATH	*
		 *******************************/

:- dynamic user:file_search_path/2.
:- multifile user:file_search_path/2.

user:(file_search_path(library, Dir) :-
	library_directory(Dir)).
user:file_search_path(swi, Home) :-
	current_prolog_flag(home, Home).
user:file_search_path(foreign, swi(ArchLib)) :-
	current_prolog_flag(arch, Arch),
	atom_concat('lib/', Arch, ArchLib).
user:file_search_path(foreign, swi(lib)).
user:file_search_path(user_profile, '.').
user:file_search_path(user_profile, app_preferences('.')).
user:file_search_path(app_preferences, PrologAppData) :-
	current_prolog_flag(windows, true),
	catch(win_folder(appdata, AppData), _, fail),
	atom_concat(AppData, '/SWI-Prolog', PrologAppData),
	(   exists_directory(PrologAppData)
	->  true
	;   catch(make_directory(PrologAppData), _, fail)
	).
user:file_search_path(app_preferences, UserHome) :-
	catch(expand_file_name(~, [UserHome]), _, fail).


%	expand_file_search_path(+Spec, -Expanded)
%
%	Expand a search path.  The system uses depth-first search upto a
%	specified depth.  If this depth is exceeded an exception is raised.
%	TBD: bread-first search?

expand_file_search_path(Spec, Expanded) :-
	catch('$expand_file_search_path'(Spec, Expanded, 0, []),
	      loop(Used),
	      throw(error(loop_error(Spec), file_search(Used)))).

'$expand_file_search_path'(Spec, Expanded, N, Used) :-
	functor(Spec, Alias, 1),
	user:file_search_path(Alias, Exp0),
	NN is N + 1,
	(   NN > 16
	->  throw(loop(Used))
	;   true
	),
	'$expand_file_search_path'(Exp0, Exp1, NN, [Alias=Exp0|Used]),
	arg(1, Spec, Base),
	'$make_path'(Exp1, Base, Expanded).
'$expand_file_search_path'(A/B, Expanded, _, _) :- !,
	'$make_path_from_slash'(A/B, Parts, []),
	atomic_list_concat(Parts, Expanded).
'$expand_file_search_path'(Spec, Spec, _, _) :-
	atomic(Spec).

'$make_path'(Dir, File, Path) :-
	atom_concat(_, /, Dir), !,
	atom_concat(Dir, File, Path).
'$make_path'(Dir, File, Path) :-
	'$make_path_from_slash'(File, Parts, []),
	'$append'([Dir, /], Parts, AllParts),
	atomic_list_concat(AllParts, Path).


%%	'$make_path_from_slash'(+SlashPath, -Parts, ?Tail) is det.
%
%	Translate a/b/c into [a,/,b,/,c]. We cannot   use DCG as the DCG
%	compiler is not yet defined.

'$make_path_from_slash'(A/B, P0, P) :- !,
	'$make_path_from_slash'(A, P0, [/|P1]),
	'$make_path_from_slash'(B, P1, P).
'$make_path_from_slash'(A, [A|T], T).


		/********************************
		*         FILE CHECKING         *
		*********************************/

%%	absolute_file_name(+Term, +Args, -AbsoluteFile) is nondet.
%%	absolute_file_name(+Term, -AbsoluteFile, +Args) is nondet.
%
%	Translate path-specifier into a full   path-name. This predicate
%	originates from Quintus was introduced  in SWI-Prolog very early
%	and  has  re-appeared  in  SICStus  3.9.0,  where  they  changed
%	argument order and  added  some   options.  As  arguments aren't
%	really ambiguous we swap the arguments if we find the new order.
%	The  SICStus  options  file_type(source)   and  relative_to  are
%	supported as well.

absolute_file_name(Spec, Args, Path) :-
	is_list(Path),
	\+ is_list(Args), !,
	absolute_file_name(Spec, Path, Args).
absolute_file_name(Spec, Args, Path) :-
	(   is_list(Args)
	->  true
	;   throw(error(type_error(list, Args), _))
	),
	(   '$select'(extensions(Exts), Args, Conditions)
	->  true
	;   memberchk(file_type(Type), Args)
	->  '$file_type_extensions'(Type, Exts),
	    Conditions = Args
	;   Conditions = Args,
	    Exts = ['']
	),
	(   '$select'(solutions(Sols), Conditions, C1)
	->  true
	;   Sols = first,
	    C1 = Conditions
	),
	(   '$select'(file_errors(FileErrors), C1, C2)
	->  true
	;   FileErrors = error,
	    C2 = C1
	),
	(   atomic(Spec),
	    '$select'(expand(true), C2, C3)
	->  expand_file_name(Spec, List),
	    '$member'(Spec1, List)
	;   Spec1 = Spec,
	    C3 = C2
	),
	(   '$chk_file'(Spec1, Exts, C3, Path)
	*-> (   Sols == first
	    ->  !
	    ;   true
	    )
	;   (   FileErrors == fail
	    ->  fail
	    ;   throw(error(existence_error(source_sink, Spec), _))
	    )
	).

'$file_type_extensions'(source, Exts) :- !, 	% SICStus 3.9 compatibility
	'$file_type_extensions'(prolog, Exts).
'$file_type_extensions'(Type, Exts) :-
	'$current_module'('$bags', _File), !,
	findall(Ext, user:prolog_file_type(Ext, Type), Exts0),
	'$append'(Exts0, [''], Exts).
'$file_type_extensions'(prolog, [pl, '']). % findall is not yet defined ...

%	user:prolog_file_type/2
%
%	Define type of file based on the extension.  This is used by
%	absolute_file_name/3 and may be used to extend the list of
%	extensions used for some type.

:- multifile(user:prolog_file_type/2),
   dynamic(user:prolog_file_type/2).

user:prolog_file_type(pl,	prolog).
user:prolog_file_type(Ext,	prolog) :-
	current_prolog_flag(associate, Ext),
	Ext \== pl.
user:prolog_file_type(qlf,	prolog).
user:prolog_file_type(qlf,	qlf).
user:prolog_file_type(Ext,	executable) :-
	current_prolog_flag(shared_object_extension, Ext).

%	File is a specification of a Prolog source file. Return the full
%	path of the file.

'$chk_file'(Spec, Extensions, Cond, FullName) :-
	'$canonise_extensions'(Extensions, Exts),
	'$dochk_file'(Spec, Exts, Cond, FullName).

'$dochk_file'(Spec, Extensions, Cond, FullName) :-
	compound(Spec),
	functor(Spec, Alias, 1),
	user:file_search_path(Alias, _), !,
	'$relative_to'(Cond, cwd, CWD),
	'$chk_alias_file'(Spec, Extensions, Cond, CWD, FullName).
'$dochk_file'(Term, Ext, Cond, FullName) :-	% allow a/b, a-b, etc.
	\+ atomic(Term), !,
	term_to_atom(Term, Raw),
	atom_chars(Raw, S0),
	'$delete'(S0, ' ', S1),
	atom_chars(Atom, S1),
	'$dochk_file'(Atom, Ext, Cond, FullName).
'$dochk_file'(File, Exts, Cond, FullName) :-
	is_absolute_file_name(File), !,
	'$extend_file'(File, Exts, Extended),
	'$file_condition'(Cond, Extended),
	'$absolute_file_name'(Extended, FullName).
'$dochk_file'(File, Exts, Cond, FullName) :-
	'$relative_to'(Cond, source, Dir),
	atomic_list_concat([Dir, /, File], AbsFile),
	'$extend_file'(AbsFile, Exts, Extended),
	'$file_condition'(Cond, Extended), !,
	'$absolute_file_name'(Extended, FullName).
'$dochk_file'(File, Exts, Cond, FullName) :-
	'$extend_file'(File, Exts, Extended),
	'$file_condition'(Cond, Extended),
	'$absolute_file_name'(Extended, FullName).

%	'$relative_to'(+Condition, +Default, -Dir)
%
%	Determine the directory to work from.  This can be specified
%	explicitely using one or more relative_to(FileOrDir) options
%	or implicitely relative to the working directory or current
%	source-file.

'$relative_to'(Conditions, Default, Dir) :-
	(   '$member'(relative_to(FileOrDir), Conditions)
	*-> (   exists_directory(FileOrDir)
	    ->	Dir = FileOrDir
	    ;	atom_concat(Dir, /, FileOrDir)
	    ->	true
	    ;	file_directory_name(FileOrDir, Dir)
	    )
	;   Default == cwd
	->  working_directory(Dir, Dir)
	;   Default == source
	->  source_location(ContextFile, _Line),
	    file_directory_name(ContextFile, Dir)
	).

:- dynamic
	'$search_path_file_cache'/5.
:- volatile
	'$search_path_file_cache'/5.

:- set_prolog_flag(verbose_file_search, false).

'$chk_alias_file'(Spec, Exts, Cond, CWD, FullFile) :-
	'$search_path_file_cache'(Spec, Cond, CWD, FullFile, Exts),
	'$file_condition'(Cond, FullFile),
	'$search_message'(file_search(cache(Spec, Cond), FullFile)).
'$chk_alias_file'(Spec, Exts, Cond, CWD, FullFile) :-
	expand_file_search_path(Spec, Expanded),
	'$extend_file'(Expanded, Exts, LibFile),
	(   '$file_condition'(Cond, LibFile),
	    '$absolute_file_name'(LibFile, FullFile),
	    \+ '$search_path_file_cache'(Spec, Cond, CWD, FullFile, Exts),
	    assert('$search_path_file_cache'(Spec, Cond, CWD, FullFile, Exts))
	->  '$search_message'(file_search(found(Spec, Cond), FullFile))
	;   '$search_message'(file_search(tried(Spec, Cond), LibFile)),
	    fail
	).

'$search_message'(Term) :-
	current_prolog_flag(verbose_file_search, true), !,
	print_message(informational, Term).
'$search_message'(_).



%	'$file_condition'(+Condition, +Path)
%
%	Verify Path satisfies Condition.

'$file_condition'([], _) :- !.
'$file_condition'([H|T], File) :- !,
	'$file_condition'(H, File),
	'$file_condition'(T, File).
'$file_condition'(exists, File) :- !,
	exists_file(File).
'$file_condition'(file_type(directory), File) :- !,
	exists_directory(File).
'$file_condition'(file_type(_), File) :- !,
	\+ exists_directory(File).
'$file_condition'(access([A1|AT]), File) :- !,
	'$file_condition'(access(A1), File),
	'$file_condition'(access(AT), File).
'$file_condition'(access([]), _) :- !.
'$file_condition'(access(Access), File) :- !,
	access_file(File, Access).
'$file_condition'(relative_to(_), _File).		% This isn't a condition

'$extend_file'(File, Exts, FileEx) :-
	'$ensure_extensions'(Exts, File, Fs),
	'$list_to_set'(Fs, FsSet),
	'$member'(FileEx, FsSet).

'$ensure_extensions'([], _, []).
'$ensure_extensions'([E|E0], F, [FE|E1]) :-
	file_name_extension(F, E, FE),
	'$ensure_extensions'(E0, F, E1).

'$list_to_set'([], []).
'$list_to_set'([H|T], R) :-
	memberchk(H, T), !,
	'$list_to_set'(T, R).
'$list_to_set'([H|T], [H|R]) :-
	'$list_to_set'(T, R).

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
Canonise the extension list. Old SWI-Prolog   require  `.pl', etc, which
the Quintus compatibility  requests  `pl'.   This  layer  canonises  all
extensions to .ext
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

'$canonise_extensions'([], []) :- !.
'$canonise_extensions'([H|T], [CH|CT]) :- !,
	'$canonise_extension'(H, CH),
	'$canonise_extensions'(T, CT).
'$canonise_extensions'(E, [CE]) :-
	'$canonise_extension'(E, CE).

'$canonise_extension'('', '') :- !.
'$canonise_extension'(DotAtom, DotAtom) :-
	atom_concat('.', _, DotAtom), !.
'$canonise_extension'(Atom, DotAtom) :-
	atom_concat('.', Atom, DotAtom).


		/********************************
		*            CONSULT            *
		*********************************/

:- user:(dynamic
	 	library_directory/1,
	        prolog_load_file/2).
:- user:(multifile
	 	library_directory/1,
	        prolog_load_file/2).


:-	flag('$break_level',	_, 0),
	flag('$compiling',	_, database),
	flag('$directive',	_, database),
	flag('$preprocessor',	_, none),
	prompt(_, '|: ').

%	compiling
%
%	Is true if SWI-Prolog is generating a state or qlf file or
%	executes a `call' directive while doing this.

compiling :-
	\+ (   flag('$compiling', database, database),
	       flag('$directive', database, database)
	   ).

:- meta_predicate
	'$ifcompiling'(0).

'$ifcompiling'(_) :-
	flag('$compiling', database, database), !.
'$ifcompiling'(G) :-
	G.

		/********************************
		*         PREPROCESSOR          *
		*********************************/

preprocessor(Old, New) :-
	flag('$preprocessor', Old, New).

'$set_encoding'(Stream, Options) :-
	memberchk(encoding(Enc), Options),
	Enc \== default,
	set_stream(Stream, encoding(Enc)).
'$set_encoding'(_, _).


%%	'$open_source'(+Spec, -In, :Goal, +Options) is semidet.

'$open_source'(stream(Id, In), In, Goal, Options) :- !,
	'$push_input_context',
	'$set_encoding'(In, Options),
	set_stream(In, file_name(Id)),
	set_stream(In, record_position(true)),
	'$open_source_call'(Id, In, Goal, True),
	'$pop_input_context',
	True == yes.
'$open_source'(File, In, Goal, Options) :-
	preprocessor(none, none), !,
	'$push_input_context',
	open(File, read, In),
	'$set_encoding'(In, Options),
	'$open_source_call'(File, In, Goal, True),
	close(In),
	'$pop_input_context',
	True == yes.
'$open_source'(File, In, Goal, Options) :-
	preprocessor(Pre, Pre),
	(   '$substitute_atom'('%f', File, Pre, Command)
	->  '$push_input_context',
	    open(pipe(Command), read, In),
	    '$set_encoding'(In, Options),
	    '$open_source_call'(File, In, Goal, True),
	    close(In),
	    '$pop_input_context',
	    True == yes
	;   throw(error(domain_error(preprocessor, Pre), _))
	).


:- dynamic
	'$load_input'/2.
:- volatile
	'$load_input'/2.

'$open_source_call'(File, In, Goal, Status) :-
	flag('$compilation_level', Level, Level+1),
	asserta('$load_input'(File, In), Ref),
	(   catch(Goal, E,
		  (print_message(error, E),
		   fail))
	->  Status = yes
	;   Status = no
	),
	erase(Ref),
	flag('$compilation_level', _, Level).


%	'$substitute_atom'(+From, +To, +In, -Out)

'$substitute_atom'(From, To, In, Out) :-
	sub_atom(In, B, _, A, From),
	sub_atom(In, 0, B, _, Before),
	sub_atom(In, _, A, 0, After),
	atomic_list_concat([Before, To, After], Out).


		 /*******************************
		 *	    DERIVED FILES	*
		 *******************************/

:- dynamic
	'$derived_source_db'/3.		% Loaded, DerivedFrom, Time

'$register_derived_source'(_, '-') :- !.
'$register_derived_source'(Loaded, DerivedFrom) :-
	retractall('$derived_source_db'(Loaded, _, _)),
	time_file(DerivedFrom, Time),
	assert('$derived_source_db'(Loaded, DerivedFrom, Time)).

%	Auto-importing dynamic predicates is not very elegant and
%	leads to problems with qsave_program/[1,2]

'$derived_source'(Loaded, DerivedFrom, Time) :-
	'$derived_source_db'(Loaded, DerivedFrom, Time).


		/********************************
		*       LOAD PREDICATES         *
		*********************************/

:- meta_predicate
	ensure_loaded(:),
	[:|+],
	consult(:),
	use_module(:),
	use_module(:, +),
	reexport(:),
	reexport(:, +),
	load_files(:),
	load_files(:, +).

%	ensure_loaded(+File|+ListOfFiles)
%
%	Load specified files, provided they where not loaded before. If the
%	file is a module file import the public predicates into the context
%	module.

ensure_loaded(Files) :-
	load_files(Files, [if(not_loaded)]).

%	use_module(+File|+ListOfFiles)
%
%	Very similar to ensure_loaded/1, but insists on the loaded file to
%	be a module file. If the file is already imported, but the public
%	predicates are not yet imported into the context module, then do
%	so.

use_module(Files) :-
	load_files(Files, [ if(not_loaded),
			    must_be_module(true)
			  ]).

%	use_module(+File, +ImportList)
%
%	As use_module/1, but takes only one file argument and imports only
%	the specified predicates rather than all public predicates.

use_module(File, Import) :-
	load_files(File, [ if(not_loaded),
			   must_be_module(true),
			   imports(Import)
			 ]).

%%	reexport(+Files)
%
%	As use_module/1, exporting all imported predicates.

reexport(Files) :-
	load_files(Files, [ if(not_loaded),
			    must_be_module(true),
			    reexport(true)
			  ]).

%%	reexport(+File, +ImportList)
%
%	As use_module/1, re-exporting all imported predicates.

reexport(File, Import) :-
	load_files(File, [ if(not_loaded),
			   must_be_module(true),
			   imports(Import),
			   reexport(true)
			 ]).


[X] :- !,
	consult(X).
[M:F|R] :-
	consult(M:[F|R]).
[].

consult(M:X) :-
	X == user, !,
	flag('$user_consult', N, N+1),
	NN is N + 1,
	atom_concat('user://', NN, Id),
	load_files(M:Id, [stream(user_input)]).
consult(List) :-
	load_files(List).

%%	'$consult_goal'(+Path, -Goal)
%
%	Determine how to load the indicated file

'$consult_goal'(Path, Goal) :-
	file_name_extension(_, Ext, Path),
	(   user:prolog_file_type(Ext, qlf)
	->  Goal = system:'$qload_file'
	;   Goal = system:'$consult_file'
	).

%%	load_files(:File, +Options)
%
%	Common entry for all the consult derivates.  File is the raw user
%	specified file specification, possibly tagged with the module.
%
%	`Options' is a list of additional options.  Defined values are
%
%	    verbose		Print statistics on user channel
%	    is_module		File MUST be a module file
%	    import = List	List of predicates to import

load_files(Files) :-
	load_files(Files, []).
load_files(Module:Files, Options) :-
        '$load_files'(Files, Module, Options).

'$load_files'(Id, Module, Options) :-	% load_files(foo, [stream(In)])
	memberchk(stream(_), Options), !,
	(   atom(Id)
	->  '$load_file'(Id, Module, Options)
	;   throw(error(type_error(atom, Id), _))
	).
'$load_files'(X, _, _) :-
	var(X), !,
	throw(error(instantiation_error, context(load_files/2,_))).
'$load_files'([], _, _) :- !.
'$load_files'([H|T], Module, Options) :- !,
	'$load_files'(H, Module, Options),
	'$load_files'(T, Module, Options).
'$load_files'(Spec, Module, Options) :-
	atom(Spec),
	'$get_option'(expand(Expand), Options, true),
	Expand == true, !,
	expand_file_name(Spec, Files),
	'$load_files'(Files, Module, [expand(false)|Options]).
'$load_files'(File, Module, Options) :-
	strip_module(Module:File, Into, PlainFile),
	'$load_file'(PlainFile, Into, Options).


'$get_option'(Term, Options, Default) :-
	arg(1, Term, Value),
	functor(Term, Name, 1),
	functor(Gen, Name, 1),
	arg(1, Gen, GVal),
	(   memberchk(Gen, Options)
	->  Value = GVal
	;   Value = Default
	).


'$noload'(true, _) :- !,
	fail.
'$noload'(not_loaded, FullFile) :-
	source_file(FullFile), !.
'$noload'(changed, Derived) :-
	'$derived_source'(_FullFile, Derived, LoadTime),
	time_file(Derived, Modified),
        Modified @=< LoadTime, !.
'$noload'(changed, FullFile) :-
	'$time_source_file'(FullFile, LoadTime, user),
        time_file(FullFile, Modified),
        Modified @=< LoadTime, !.

%	'$qlf_file'(+Spec, +PlFile, -LoadFile)
%
%	Return the QLF file if it exists.  Might check for modification
%	time, version, etc.
%
%	If the user-specification specified a prolog file, do not
%	replace this with a .qlf file.

'$qlf_file'(Spec, FullFile, FullFile) :-
	'$spec_extension'(Spec, Ext),
	user:prolog_file_type(Ext, prolog), !.
'$qlf_file'(_, FullFile, QlfFile) :-
	flag('$compiling', database, database),
	file_name_extension(Base, PlExt, FullFile),
	user:prolog_file_type(PlExt, prolog),
	user:prolog_file_type(QlfExt, qlf),
	file_name_extension(Base, QlfExt, QlfFile),
	access_file(QlfFile, read).
'$qlf_file'(_, FullFile, FullFile).


'$spec_extension'(File, Ext) :-
	atom(File),
	file_name_extension(_, Ext, File).
'$spec_extension'(Spec, Ext) :-
	compound(Spec),
	arg(1, Spec, Arg),
	'$spec_extension'(Arg, Ext).


%%	'$load_file'(+Spec, +ContextModule, +Options) is det.
%
%	Load the file Spec  into   ContextModule  controlled by Options.
%	This wrapper deals with two cases  before proceeding to the real
%	loader:
%
%	    * User hooks based on prolog_load_file/2
%	    * The file is already loaded.

'$load_file'(File, Module, Options) :-
	\+ memberchk(stream(_), Options),
	user:prolog_load_file(Module:File, Options), !.
'$load_file'(File, Module, Options) :-
	(   memberchk(stream(FromStream), Options)
	->  FullFile = File
	;   absolute_file_name(File,
			       [ file_type(prolog),
				 access(read)
			       ],
			       FullFile)
	),

	'$get_option'(if(If), Options, true),

	(   var(FromStream),
	    '$noload'(If, FullFile)
	->  (   '$current_module'(LoadModule, FullFile)
	    ->  '$import_from_loaded_module'(LoadModule, Module, Options)
	    ;   (   Module == user
		->  true
		;   '$load_file'(File, Module, [if(true)|Options])
		)
	    )
	;   with_mutex('$load',
		       '$do_load_file'(File, FullFile, Module, Options)),
	    '$run_initialization'(FullFile)
	).




%%	'$do_load_file'(+Spec, +FullFile, +ContextModule, +Options) is det.
%
%	Perform the actual loading. This process is guarded by the mutex
%	=|$load|=

'$do_load_file'(File, FullFile, Module, Options) :-
	statistics(heapused, OldHeap),
	statistics(cputime, OldTime),

	'$set_verbose_load'(Options, OldVerbose),
	'$update_autoload_level'(Options, OldAutoLevel),
	'$get_option'(derived_from(DerivedFrom), Options, -),

	current_prolog_flag(generate_debug_info, DebugInfo),

	(   memberchk(stream(FromStream), Options)
	->  Absolute = File
	;   '$qlf_file'(File, FullFile, Absolute)
	),

	flag('$compilation_level', Level, Level),
	'$load_message_level'(MessageLevel),

	'$print_message'(silent /*MessageLevel*/,
			 load_file(start(Level,
					 file(File, Absolute)))),
	(   nonvar(FromStream),
	    (   '$get_option'(format(qlf), Options, source)
	    ->  set_stream(FromStream, file_name(Absolute)),
		'$qload_stream'(FromStream, Module, Action, LM, Options)
	    ;   '$consult_file'(stream(Absolute, FromStream),
				Module, Action, LM, Options)
	    )
	->  true
	;   var(FromStream),
	    '$consult_goal'(Absolute, Goal),
	    call(Goal, Absolute, Module, Action, LM, Options)
	->  true
	;   print_message(error, load_file(failed(File))),
	    fail
	),

	'$import_from_loaded_module'(LM, Module, Options),

	(   Level == 0
	->  garbage_collect_clauses
	;   true
	),

	'$register_derived_source'(Absolute, DerivedFrom),

	statistics(heapused, Heap),
	statistics(cputime, Time),
	HeapUsed is Heap - OldHeap,
	TimeUsed is Time - OldTime,

	'$print_message'(MessageLevel,
			 load_file(done(Level,
					file(File, Absolute),
					Action,
					LM,
					TimeUsed,
					HeapUsed))),
	flag('$autoloading', _, OldAutoLevel),
	set_prolog_flag(verbose_load, OldVerbose),
	set_prolog_flag(generate_debug_info, DebugInfo).


%%	'$import_from_loaded_module'(LoadedModule, Module, Options) is det.
%
% 	Import public predicates from LoadedModule into Module

'$import_from_loaded_module'(LoadedModule, Module, Options) :-
	atom(LoadedModule), !,
	'$get_option'(imports(Import), Options, all),
	'$get_option'(reexport(Reexport), Options, false),
	'$import_list'(Module, LoadedModule, Import, Reexport).
'$import_from_loaded_module'(_, _, _).


%%	'$set_verbose_load'(+Options, -Old) is det.
%
%	Set the =verbose_load= flag according to   Options and unify Old
%	with the old value.

'$set_verbose_load'(Options, Old) :-
	current_prolog_flag(verbose_load, Old),
	'$negate'(Old, DefSilent),
	'$get_option'(silent(Silent), Options, DefSilent),
	'$negate'(Silent, Verbose),
	set_prolog_flag(verbose_load, Verbose).

'$negate'(false, true).
'$negate'(true,  false).

%%	'$update_autoload_level'(+Options, -OldLevel)
%
%	Update the $autoloading flag and return the old value.

'$update_autoload_level'(Options, AutoLevel) :-
	'$get_option'(autoload(Autoload), Options, false),
	(   Autoload == false
	->  flag('$autoloading', AutoLevel, AutoLevel)
	;   flag('$autoloading', AutoLevel, AutoLevel+1)
	).

%%	'$load_message_level'(-MessageLevel) is det.
%
%	Compute the verbosity-level for loading this file.

'$load_message_level'(MessageLevel) :-
	(   current_prolog_flag(verbose_load, true),
	    (   flag('$autoloading', 0, 0)
	    ;   current_prolog_flag(verbose_autoload, true)
	    )
	->  MessageLevel = informational
	;   MessageLevel = silent
	).

%%	'$print_message'(+Level, +Term) is det.
%
%	As print_message/2, but deal with  the   fact  that  the message
%	system might not yet be loaded.

'$print_message'(Level, Term) :-
	'$current_module'('$messages', _), !,
	print_message(Level, Term).
'$print_message'(_Level, _Term).

'$print_message_fail'(E) :-
	'$print_message'(error, E),
	fail.

%%	'$consult_file'(+Path, +Module, -Action, -LoadedIn, +Options)
%
%	Called  from  '$do_load_file'/4  using  the   goal  returned  by
%	'$consult_goal'/2. This means that the  calling conventions must
%	be kept synchronous with '$qload_file'/6.

'$consult_file'(Absolute, Module, What, LM, Options) :-
	'$set_source_module'(Module, Module), !, % same module
	'$consult_file_2'(Absolute, Module, What, LM, Options).
'$consult_file'(Absolute, Module, What, LM, Options) :-
	'$set_source_module'(OldModule, Module),
	'$ifcompiling'('$qlf_start_sub_module'(Module)),
        '$consult_file_2'(Absolute, Module, What, LM, Options),
	'$ifcompiling'('$qlf_end_part'),
	'$set_source_module'(_, OldModule).

'$consult_file_2'(Absolute, Module, What, LM, Options) :-
	'$set_source_module'(OldModule, Module),	% Inform C we start loading
	'$load_id'(Absolute, Id),
	'$start_consult'(Id),
	'$compile_type'(What),
	(   flag('$compiling', wic, wic)	% TBD
	->  '$add_directive_wic'('$assert_load_context_module'(Id, OldModule))
	;   true
	),
	'$assert_load_context_module'(Id, OldModule),


	'$save_lex_state'(LexState),
	'$open_source'(Absolute, In,
		       '$load_file'(In, Id, LM, Options),
		       Options),
	'$restore_lex_state'(LexState),
	'$set_source_module'(_, OldModule).	% Restore old module

:- thread_local
	'$current_load_dialect'/1.

%%	'$set_dialect'(-Old, +New) is det.
%
%	Switch the currently expected dialect.

'$set_dialect'(Old, New) :-
	'$my_dialect'(Old),
	(   Old == New
	->  true
	;   retractall('$current_load_dialect'(_)),
	    (	New == swi
	    ->	true
	    ;	assert('$current_load_dialect'(New))
	    )
	).

'$my_dialect'(Current) :-
	(   '$current_load_dialect'(Dialect)
	->  Current = Dialect
	;   Current = swi
	).

'$save_lex_state'(lexstate(Style, Dialect)) :-
	'$style_check'(Style, Style),
	'$set_dialect'(Dialect, Dialect).
'$restore_lex_state'(lexstate(Style, Dialect)) :-
	'$style_check'(_, Style),
	'$set_dialect'(_, Dialect).


'$load_id'(stream(Id, _), Id) :- !.
'$load_id'(Id, Id).

'$compile_type'(What) :-
	flag('$compiling', How, How),
	(   How == database
	->  What = compiled
	;   How == qlf
	->  What = '*qcompiled*'
	;   What = 'boot compiled'
	).

%%	'$load_context_module'(+File, -Module)
%
%	Record the module a file was loaded from (see make/0)
%
%	@tbd	Should also know which predicates are imported!

:- dynamic
	'$load_context_module'/2.

'$assert_load_context_module'(File, Module) :-
	(   '$load_context_module'(File, Module)
	->  true
	;   assert('$load_context_module'(File, Module))
	).


%%   '$load_file'(+In, +Path, -Module, +Options)
%
%   '$load_file'/4 does the actual loading.
%
%   @tbd We should watch-out for an :- encoding/1 directive that can
%   preceed the :- module/2 header!

'$load_file'(In, File, Module, Options) :-
	(   peek_char(In, #)
	->  skip(In, 10)
	;   true
	),
	'$read_clause'(In, First),
	'$load_file'(First, In, File, Module, Options).


'$load_file'((?- module(Module, Public)), In, File, Module, Options) :- !,
	'$load_module'(Module, Public, In, File, Options).
'$load_file'((:- module(Module, Public)), In, File, Module, Options) :- !,
	'$load_module'(Module, Public, In, File, Options).
'$load_file'((?- module(Module, Public)),
	     In, File, Module, Options) :- !,
	'$load_module'(Module, Public, In, File, Options).
'$load_file'((:- module(Module, Public)),
	     In, File, Module, Options) :- !,
	'$load_module'(Module, Public, In, File, Options).
'$load_file'(_, _, File, _, Options) :-
	'$get_option'(must_be_module(true), Options, false), !,
	throw(error(domain_error(module_file, File), _)).
'$load_file'(end_of_file, _, File, Module, _) :- !, 	% empty file
	'$set_source_module'(Module, Module),
	'$ifcompiling'('$qlf_start_file'(File)),
	'$ifcompiling'('$qlf_end_part').
'$load_file'(FirstClause, In, File, Module, _Options) :- !,
	'$set_source_module'(Module, Module),
	'$ifcompiling'('$qlf_start_file'(File)),
	ignore('$consult_clause'(FirstClause, File)),
	'$consult_stream'(In, File),
	'$ifcompiling'('$qlf_end_part').

'$reserved_module'(system).
'$reserved_module'(user).

%%	'$load_module'(+Module, +Public, +Stream, +File, +Options)
%
%	Options processed:
%
%		* redefine_module(Action)
%		Action is one of =true=, =false= or =ask=.
%
%	Redefining a module by loading another file must be more subtle.
%	Verify the compatibility of the interface could be one example.

'$load_module'(Reserved, _, _, _, _) :-
	'$reserved_module'(Reserved), !,
	throw(error(permission_error(load, module, Reserved), _)).
'$load_module'(Module, Public, In, File, Options) :-
	'$set_source_module'(OldModule, OldModule),
	source_location(_File, Line),
	'$get_option'(redefine_module(Action), Options, false),
	'$super_module'(File, Super),
	'$declare_module'(Module, Super, File, Line, Action),
	'$export_list'(Public, Module, Ops),
	'$ifcompiling'('$qlf_start_module'(Module)),
	'$export_ops'(Ops, Module, File),
	'$consult_stream'(In, File),
	'$check_export'(Module),
	'$ifcompiling'('$qlf_end_part').

%%	'$super_module'(+File, -Super) is det.
%
%	Determine the initial module from which   I  inherit. All system
%	and library modules inherit from =system=, while all normal user
%	modules inherit from =user=.

'$super_module'(File, system) :-
	current_prolog_flag(home, Home),
	sub_atom(File, 0, _, _, Home), !.
'$super_module'(_, user).

'$check_export'(Module) :-
	'$undefined_export'(Module, UndefList),
	(   '$member'(Undef, UndefList),
	    strip_module(Undef, _, Local),
	    print_message(error,
			  undefined_export(Module, Local)),
	    fail
	;   true
	).


%%	'$import_list'(+TargetModule, +FromModule, +Import, +Reexport) is det.
%
%	Import from FromModule to TargetModule. Import  is one of =all=,
%	a list of optionally  mapped  predicate   indicators  or  a term
%	except(Import).

'$import_list'(_, _, Var, _) :-
	var(Var), !,
	throw(error(instantitation_error, _)).
'$import_list'(Target, Source, all, Reexport) :- !,
	'$module_property'(Source, exports(Import)),
	'$import_ops'(Target, Source),
	'$import_list'(Target, Source, Import, Reexport).
'$import_list'(Target, Source, except(Spec), Reexport) :- !,
	'$module_property'(Source, exports(Export)),
	(   is_list(Spec)
	->  true
	;   throw(error(type_error(list, Spec), _))
	),
	'$import_except'(Spec, Export, Import),
	'$import_list'(Target, Source, Import, Reexport).
'$import_list'(Target, Source, Import, Reexport) :- !,
	is_list(Import), !,
	'$import_all'(Import, Target, Source, Reexport).
'$import_list'(_, _, Import, _) :-
	throw(error(type_error(import_specifier, Import))).


'$import_except'([], List, List).
'$import_except'([H as N|T], List0, List) :- !,
	'$import_as'(H, N, List0, List1),
	'$import_except'(T, List1, List).
'$import_except'([H|T], List0, List) :-
	'$select'(P, List0, List1),
	'$same_pi'(H, P), !,
	'$import_except'(T, List1, List).

'$import_as'(PI, N, [PI2|T], [PI as N|T]) :-
	'$same_pi'(PI, PI2), !.
'$import_as'(PI, N, [H|T0], [H|T]) :- !,
	'$import_as'(PI, N, T0, T).
'$import_as'(PI, _, _, _) :-
	throw(error(existence_error(export, PI), _)).

'$same_pi'(PI1, PI2) :-
	'$canonical_pi'(PI1, PI),
	'$canonical_pi'(PI2, PI).

'$canonical_pi'(N//A0, N/A) :- !,
	A is A0 + 2.
'$canonical_pi'(PI, PI).


'$import_all'(Import, Context, Source, Reexport) :-
	'$import_all2'(Import, Context, Source, Imported),
	(   Reexport == true,
	    '$list_to_conj'(Imported, Conj)
	->  export(Context:Conj),
	    (	flag('$compiling', wic, wic)
	    ->	'$add_directive_wic'(export(Context:Conj))
	    ;	true
	    )
	;   true
	).

'$import_all2'([], _, _, []).
'$import_all2'([PI as NewName|Rest], Context, Source,
	       [NewName/Arity|Imported]) :- !,
	'$canonical_pi'(PI, Name/Arity),
	length(Args, Arity),
	Head =.. [Name|Args],
	NewHead =.. [NewName|Args],
	source_location(File, _Line),
	(   '$get_predicate_attribute'(Source:Head, transparent, 1)
	->  '$set_predicate_attribute'(Context:NewHead, transparent, 1)
	;   true
	),
	'$store_clause'((NewHead :- Source:Head), File),
	'$import_all2'(Rest, Context, Source, Imported).
'$import_all2'([Pred|Rest], Context, Source, [Pred|Imported]) :-
	Context:import(Source:Pred),
	'$import_wic'(Source, Pred),
	'$import_all2'(Rest, Context, Source, Imported).


'$list_to_conj'([One], One) :- !.
'$list_to_conj'([H|T], (H,Rest)) :-
	'$list_to_conj'(T, Rest).

%%	'$import_ops'(+Target, +Source)
%
%	Import the operators export from Source into the module table of
%	Target.

'$import_ops'(To, From) :-
	(   '$c_current_predicate'(_, From:'$exported_op'(_, _, _)),
	    From:'$exported_op'(Pri, Assoc, Name),
	    op(Pri, Assoc, To:Name),
	    fail
	;   true
	).


%%	'$export_list'(+Declarations, +Module, -Ops)
%
%	Handle the export list of the module declaration for Module
%	associated to File.

'$export_list'(Decls, Module, Ops) :-
	is_list(Decls), !,
	'$do_export_list'(Decls, Module, Ops).
'$export_list'(Decls, _, _) :-
	var(Decls),
	throw(error(instantiation_error, _)).
'$export_list'(Decls, _, _) :-
	throw(error(type_error(list, Decls), _)).

'$do_export_list'([], _, []) :- !.
'$do_export_list'([H|T], Module, Ops) :- !,
	'$export1'(H, Module, Ops, Ops1),
	'$do_export_list'(T, Module, Ops1).

'$export1'(Var, _, _, _) :-
	var(Var), !,
	throw(error(instantiation_error, _)).
'$export1'(Op, _, [Op|T], T) :-
	Op = op(_,_,_), !.
'$export1'(PI, Module, Ops, Ops) :-
	export(Module:PI).

'$export_ops'([op(Pri, Assoc, Name)|T], Module, File) :-
	op(Pri, Assoc, Module:Name),
	'$store_clause'('$exported_op'(Pri, Assoc, Name), File),
	'$export_ops'(T, Module, File).
'$export_ops'([], _, _).


%%	'$consult_stream'(+Stream, +File)
%
%	Read and record all clauses until the rest of the file.

'$consult_stream'(In, File) :-
	repeat,
	catch('$consult_stream2'(In, File),
	      E,
	      (	  print_message(error, E),
		  fail
	      )), !.


'$consult_stream2'(In, File) :-
	repeat,
	    '$read_clause'(In, Clause),
	    '$expand_term'(Clause, Expanded),
	    '$store_clause'(Expanded, File),
	    Clause == end_of_file, !.

'$consult_clause'(Clause, File) :-
	catch(('$expand_term'(Clause, Expanded),
	       '$store_clause'(Expanded, File)),
	       E,
	       '$print_message_fail'(E)).

%%	'$execute_directive'(:Goal, +File) is det.
%
%	Execute the argument of :- or ?- while loading a file.

'$execute_directive'(Goal, F) :-
	'$expand_goal'(Goal, Goal1),
	'$execute_directive_2'(Goal1, F).

'$execute_directive_2'(include(File), F) :- !,
	'$expand_include'(File, F).
'$execute_directive_2'(encoding(Encoding), F) :- !,
	source_location(F, _),
	'$load_input'(F, S),
	set_stream(S, encoding(Encoding)).
'$execute_directive_2'(ISO, F) :-
	'$expand_directive'(ISO, Normal), !,
	'$execute_directive'(Normal, F).
'$execute_directive_2'(Goal, _) :-
	\+ flag('$compiling', database, database), !,
	'$add_directive_wic2'(Goal, Type),
	(   Type == call		% suspend compiling into .qlf file
	->  flag('$compiling', Old, database),
	    flag('$directive', OldDir, Old),
	    call_cleanup('$execute_directive_3'(Goal),
			 (   flag('$compiling', _, Old),
			     flag('$directive', _, OldDir)
			 ))
	;   '$execute_directive_3'(Goal)
	).
'$execute_directive_2'(Goal, _) :-
	'$execute_directive_3'(Goal).

'$execute_directive_3'(Goal) :-
	'$set_source_module'(Module, Module),
	(   catch(Module:Goal, Term, '$exception_in_directive'(Term))
	->  true
	;   print_message(warning, goal_failed(directive, Module:Goal)),
	    fail
	).

'$exception_in_directive'(Term) :-
	print_message(error, Term),
	fail.

%	This predicate deals with the very odd ISO requirement to allow
%	for :- dynamic(a/2, b/3, c/4) instead of the normally used
%	:- dynamic a/2, b/3, c/4 or, if operators are not desirable,
%	:- dynamic((a/2, b/3, c/4)).

'$expand_directive'(Directive, Expanded) :-
	functor(Directive, Name, Arity),
	Arity > 1,
	'$iso_property_directive'(Name),
	Directive =.. [Name|Args],
	'$mk_normal_args'(Args, Normal),
	Expanded =.. [Name, Normal].

'$iso_property_directive'(dynamic).
'$iso_property_directive'(multifile).
'$iso_property_directive'(discontiguous).

'$mk_normal_args'([One], One).
'$mk_normal_args'([H|T0], (H,T)) :-
	'$mk_normal_args'(T0, T).


%	Note that the list, consult and ensure_loaded directives are already
%	handled at compile time and therefore should not go into the
%	intermediate code file.

'$add_directive_wic2'(Goal, Type) :-
	'$common_goal_type'(Goal, Type), !,
	(   Type == load
	->  true
	;   '$set_source_module'(Module, Module),
	    '$add_directive_wic'(Module:Goal)
	).
'$add_directive_wic2'(Goal, _) :-
	(   flag('$compiling', qlf, qlf)	% no problem for qlf files
	->  true
	;   print_message(error, mixed_directive(Goal))
	).

'$common_goal_type'((A,B), Type) :- !,
	'$common_goal_type'(A, Type),
	'$common_goal_type'(B, Type).
'$common_goal_type'((A;B), Type) :- !,
	'$common_goal_type'(A, Type),
	'$common_goal_type'(B, Type).
'$common_goal_type'((A->B), Type) :- !,
	'$common_goal_type'(A, Type),
	'$common_goal_type'(B, Type).
'$common_goal_type'(Goal, Type) :-
	'$goal_type'(Goal, Type).

'$goal_type'(Goal, Type) :-
	(   '$load_goal'(Goal)
	->  Type = load
	;   Type = call
	).

'$load_goal'([_|_]).
'$load_goal'(consult(_)).
'$load_goal'(load_files(_)).
'$load_goal'(load_files(_,Options)) :-
	memberchk(qcompile(true), Options).
'$load_goal'(ensure_loaded(_)) :- flag('$compiling', wic, wic).
'$load_goal'(use_module(_))    :- flag('$compiling', wic, wic).
'$load_goal'(use_module(_, _)) :- flag('$compiling', wic, wic).

		/********************************
		*        TERM EXPANSION         *
		*********************************/

'$store_clause'([], _) :- !.
'$store_clause'([C|T], F) :- !,
	'$store_clause'(C, F),
	'$store_clause'(T, F).
'$store_clause'(end_of_file, _) :- !.
'$store_clause'((:- Goal), F) :- !,
	'$execute_directive'(Goal, F).
'$store_clause'((?- Goal), F) :- !,
	'$execute_directive'(Goal, F).
'$store_clause'((_, _), _) :- !,
	print_message(error, cannot_redefine_comma),
	fail.
'$store_clause'('$source_location'(File, Line):Term, _) :- !,
	'$record_clause'(Term, File:Line, Ref),
	'$qlf_assert_clause'(Ref).
'$store_clause'(Term, File) :-
	'$record_clause'(Term, File, Ref),
        '$qlf_assert_clause'(Ref).

'$qlf_assert_clause'(_) :-
	flag('$compiling', database, database), !.
'$qlf_assert_clause'(Ref) :-
	'$qlf_assert_clause'(Ref, development).


%%	compile_aux_clauses(+Clauses) is det.
%
%	Compile clauses given the current  source   location  but do not
%	change  the  notion  of   the    current   procedure  such  that
%	discontiguous  warnings  are  not  issued.    The   clauses  are
%	associated with the current file and  therefore wiped out if the
%	file is reloaded.

compile_aux_clauses(Clauses) :-
	source_location(File, _Line),
	'$start_aux'(File, Context),
	call_cleanup('$store_clause'(Clauses, File),
		     '$end_aux'(File, Context)).


		 /*******************************
		 *	     INCLUDE		*
		 *******************************/

:- multifile
	'$included'/3.			% Into, File, LastModified
:- dynamic
	'$included'/3.

'$expand_include'(File, FileInto) :-
	absolute_file_name(File,
			   [ file_type(prolog),
			     access(read)
			   ], Path),
	'$push_input_context',
	open(Path, read, In),
	time_file(Path, Time),
	'$read_clause'(In, Term0),
	'$read_include_file'(Term0, In, Terms),
	close(In),
	'$pop_input_context',
	'$store_clause'(system:'$included'(FileInto, Path, Time), FileInto),
	'$consult_clauses'(Terms, FileInto).

'$read_include_file'(end_of_file, _, []) :- !.
'$read_include_file'(T0, In, [T0|T]) :-
	'$read_clause'(In, T1),
	'$read_include_file'(T1, In, T).

'$consult_clauses'([], _).
'$consult_clauses'([H|T], File) :-
	'$consult_clause'(H, File),
	'$consult_clauses'(T, File).


		 /*******************************
		 *	       READING		*
		 *******************************/

:- multifile
	prolog:comment_hook/3.

'$read_clause'(In, Term) :-
	'$get_predicate_attribute'(prolog:comment_hook(_,_,_),
				   number_of_clauses, N),
	N > 0, !,
	'$set_source_module'(SM, SM),
	Options0 = [ errors(dec10),
		     comments(Comments),
		     term_position(Pos),
		     module(SM)
		   ],
	'$singleton_option'(SM, Options, Options0),
	read_term(In, Term, Options),
	(   Comments \== [],
	    catch(prolog:comment_hook(Comments, Pos, Term), E,
		  print_message(error, E))
	->  true
	;   true
	).
'$read_clause'(In, Term) :-
	read_clause(In, Term).

'$singleton_option'(M, [singletons(warning)|T],T) :-
	M:'$style_check'(Old, Old),
	Old /\ 0b0000010 =\= 0, !.	% See style_check/1
'$singleton_option'(_, T, T).


		 /*******************************
		 *	 FOREIGN INTERFACE	*
		 *******************************/

%	call-back from PL_register_foreign().  First argument is the module
%	into which the foreign predicate is loaded and second is a term
%	describing the arguments.

:- dynamic
	'$foreign_registered'/2.

		 /*******************************
		 *   TEMPORARY TERM EXPANSION	*
		 *******************************/

% Provide temporary definitions for the boot-loader.  These are replaced
% by the real thing in load.pl

:- dynamic
	'$expand_goal'/2,
	'$expand_term'/2.

'$expand_goal'(In, In).
'$expand_term'(In, In).


		/********************************
		*     WIC CODE COMPILER         *
		*********************************/

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
This entry point is called from pl-main.c  if the -c option (compile) is
given. It compiles all files and finally calls qsave_program to create a
saved state.
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

'$compile_wic' :-
	current_prolog_flag(argv, Argv),
	'$get_files_argv'(Argv, Files),
	'$translate_options'(Argv, Options),
	'$option'(compileout, Out, Out),
        user:consult(Files),
	user:qsave_program(Out, Options).

'$get_files_argv'([], []) :- !.
'$get_files_argv'(['-c'|Files], Files) :- !.
'$get_files_argv'([_|Rest], Files) :-
	'$get_files_argv'(Rest, Files).

'$translate_options'([], []).
'$translate_options'([O|T0], [Opt|T]) :-
	atom_chars(O, [-,-|Rest]),
	'$split'(Rest, [=], Head, Tail), !,
	atom_chars(Name, Head),
	name(Atom, Tail),
	term_to_atom(Value, Atom),
	Opt =.. [Name, Value],
	'$translate_options'(T0, T).
'$translate_options'([_|T0], T) :-
	'$translate_options'(T0, T).

'$split'(List, Split, [], Tail) :-
	'$append'(Split, Tail, List), !.
'$split'([H|T0], Split, [H|T], Tail) :-
	'$split'(T0, Split, T, Tail).


		/********************************
		*       LIST PROCESSING         *
		*********************************/

'$member'(X, [X|T]) :-
	(   T == []
	->  !
	;   true
	).
'$member'(X, [_|T]) :-
	'$member'(X, T).

'$append'([], L, L).
'$append'([H|T], L, [H|R]) :-
	'$append'(T, L, R).

'$select'(X, [X|Tail], Tail).
'$select'(Elem, [Head|Tail], [Head|Rest]) :-
	'$select'(Elem, Tail, Rest).

'$reverse'(L1, L2) :-
	'$reverse'(L1, [], L2).

'$reverse'([], List, List).
'$reverse'([Head|List1], List2, List3) :-
	'$reverse'(List1, [Head|List2], List3).

'$delete'([], _, []) :- !.
'$delete'([Elem|Tail], Elem, Result) :- !,
	'$delete'(Tail, Elem, Result).
'$delete'([Head|Tail], Elem, [Head|Rest]) :-
	'$delete'(Tail, Elem, Rest).


		 /*******************************
		 *   HANDLE TRACER 'L'-COMMAND	*
		 *******************************/

:- multifile
	user:prolog_list_goal/1.

'$prolog_list_goal'(Goal) :-
	user:prolog_list_goal(Goal), !.
'$prolog_list_goal'(Goal) :-
	user:listing(Goal).


		 /*******************************
		 *	       HALT		*
		 *******************************/

:- '$iso'((halt/0)).

halt :-
	halt(0).


:- meta_predicate
	at_halt(0).
:- dynamic
	'$at_halt'/1.

at_halt(Goal) :-
	asserta('$at_halt'(Goal)).

'$run_at_halt' :-
	(   '$at_halt'(Goal),
	    catch(Goal, E, print_message(error, E)),
	    fail
	;   true
	).


		/********************************
		*      LOAD OTHER MODULES       *
		*********************************/

:- meta_predicate
	'$load_wic_files'(:).

'$load_wic_files'(Files) :-
	Files = Module:_,
	'$execute_directive'('$set_source_module'(OldM, Module), []),
	'$save_lex_state'(LexState),
	'$style_check'(_, 2'1111),
	flag('$compiling', OldC, wic),
	consult(Files),
	'$execute_directive'('$set_source_module'(_, OldM), []),
	'$execute_directive'('$restore_lex_state'(LexState), []),
	flag('$compiling', _, OldC).


%%	'$load_additional_boot_files' is det.
%
%	Called from compileFileList() in pl-wic.c.   Gets the files from
%	"-c file ..." and loads them into the module user.

'$load_additional_boot_files' :-
	current_prolog_flag(argv, Argv),
	'$get_files_argv'(Argv, Files),
	(   Files \== []
	->  format('Loading additional boot files~n'),
	    '$load_wic_files'(user:Files),
	    format('additional boot files loaded~n')
	;   true
        ).

'$:-'((format('Loading Prolog startup files~n', []),
       source_location(File, _Line),
       file_directory_name(File, Dir),
       atom_concat(Dir, '/load.pl', LoadFile),
       '$load_wic_files'(system:[LoadFile]),
       (   current_prolog_flag(windows, true)
       ->  atom_concat(Dir, '/menu.pl', MenuFile),
	   '$load_wic_files'(system:[MenuFile])
       ;   true
       ),
       format('SWI-Prolog boot files loaded~n', []),
       flag('$compiling', OldC, wic),
       '$execute_directive'('$set_source_module'(_, user), []),
       flag('$compiling', _, OldC)
      )).
