/*  $Id$

    Part of XPCE --- The SWI-Prolog GUI toolkit

    Author:        Jan Wielemaker and Anjo Anjewierden
    E-mail:        wielemak@science.uva.nl
    WWW:           http://www.swi-prolog/projects/xpce/
    Copyright (C): 1985-2006, University of Amsterdam

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

:- module(prolog_gui,
	  [ prolog_tracer/2,		% +Thread, -GUI
	    send_tracer/2,		% +Thread, :Goal
	    send_if_tracer/2,		% +Thread, :Goal
	    get_tracer/3,		% +Thread, :Goal, -Result
	    send_tracer/1,		% :Goal
	    send_if_tracer/1,		% :Goal
	    get_tracer/2,		% :Goal, -Result
	    in_debug_thread/2,		% +ObjOrThread, :Goal
	    prolog_frame_attribute/4	% +GUI, +Frame, +Attr, -Value
	  ]).
:- use_module(library(pce)).
:- use_module(library(lists)).
:- use_module(library(toolbar)).
:- use_module(library(pce_report)).
:- use_module(library(pce_util)).
:- use_module(library(persistent_frame)).
:- use_module(library(debug)).
:- use_module(trace).
:- use_module(clause).
:- use_module(util).
:- use_module(source).
:- consult([ settings,
	     pprint,
	     stack,
	     viewterm
	   ]).

:- multifile
	user:prolog_event_hook/1,
	user:message_hook/3.

register_directories :-
	(   member(SpyBase, ['icons/nospy', library('trace/icons/nospy')]),
	    absolute_file_name(SpyBase,
			       [ extensions([xpm]), access(read)],
			       SpyIcon)
	->  file_directory_name(SpyIcon, Icons),
	    pce_image_directory(Icons)
	),
	(   member(HlpBase, ['pltracer', library('trace/pltracer')]),
	    absolute_file_name(HlpBase,
			       [ extensions([hlp]), access(read)],
			       HlpFile)
	->  pce_help_file(pltracer, HlpFile)
	).

:- register_directories.

version('2.0').

		 /*******************************
		 *	      RESOURCES		*
		 *******************************/

resource(debug,	image,	image('debug.xpm')).

		 /*******************************
		 *	     TOPLEVEL		*
		 *******************************/

:- dynamic
	gui/3.				% +Thread, +BreakLevel, -Gui

%%	prolog_tracer(+Thread, -Ref) is det.
%%	prolog_tracer(+Thread, -Ref, Create) is semidet.
%
%	Get the Prolog debugger window for Thread.

prolog_tracer(Thread, Ref) :-
	prolog_tracer(Thread, Ref, true).

prolog_tracer(Thread, Ref, Create) :-
	break_level(Level),
	(   gui(Thread, Level, Ref)
	->  true
	;   Create == true
	->  debug('New GUI for thread ~p, break level ~p~n', [Thread, Level]),
	    send_pce(send(new(Ref, prolog_debugger(Level, Thread)), open))
	).

%%	break_level(-Level)
%
%	Current break-level.
%
%	@bug	Breaks only work in the main thread. We need to change I/O
%		handling and where the break-level is stored to fix
%		this.

break_level(Level) :-
	thread_self(main), !,
	flag('$break_level', Level, Level).
break_level(1).


%%	send_tracer(+ThreadOrGUI, +Term) is semidet.
%%	send_if_tracer(+Thread, +Term) is semidet.
%%	get_tracer(+Thread, +Term, -Reply) is semidet.
%
%	Send messages to the XPCE tracer window.
%
%	@param Thread: calling thread.

send_tracer(Term) :-
	thread_self(Thread),
	send_tracer(Thread, Term).
send_tracer(GUI, Term) :-
	object(GUI), !,
	send_pce(send(GUI, Term)).
send_tracer(Thread, Term) :-
	prolog_tracer(Thread, Ref),
	send_pce(send(Ref, Term)).

send_if_tracer(Term) :-
	thread_self(Thread),
	send_if_tracer(Thread, Term).
send_if_tracer(Thread, Term) :-
	(   prolog_tracer(Thread, Ref, false)
	->  send_pce(send(Ref, Term))
	;   true
	).

get_tracer(Term, Result) :-
	thread_self(Thread),
	get_tracer(Thread, Term, Result).
get_tracer(GUI, Term, Result) :-
	object(GUI), !,
	get(GUI, Term, Result).
get_tracer(Thread, Term, Result) :-
	prolog_tracer(Thread, Ref),
	get(Ref, Term, Result).


		 /*******************************
		 *    THREAD SYNCHRONISATION	*
		 *******************************/

:- meta_predicate
	in_debug_thread(+, :),
	send_pce(:).

%%	send_pce(:Goal)
%
%	Run Goal in XPCE (main)  thread.   Wait  for  completion. In the
%	meanwhile, allow the XPCE thread to call in_debug_thread/1.
%
%	@bug	XPCE thread does not _need_ to be =main=.

send_pce(Goal) :-
	thread_self(main), !,
	Goal.
send_pce(Goal) :-
	thread_self(Self),
	strip_module(Goal, M, G),
	term_variables(G, GVars),
	in_pce_thread(run_pce(M:G, GVars, Self)),
	repeat,
	thread_get_message('$trace'(Result)),
	debug(' ---> send_pce: result = ~p~n', [Result]),
	(   Result = error(E)
	->  throw(E)
	;   Result = call(CallBack, CBVars, Caller)
	->  run_pce(CallBack, CBVars, Caller),
	    fail
	;   Result = true(BGVars)
	->  !, BGVars = GVars
	;   assertion(Result == false), !,
	    fail
	).

run_pce(Goal, Vars, Caller) :-
	debug('Running ~p for thread ~p~n', [Goal, Caller]),
	(   catch(Goal, Error, true)
	->  (   var(Error)
	    ->	Result = true(Vars)
	    ;	Result = error(Error)
	    )
	;   Result = false
	),
	debug('Ok, returning ~p~n', [Result]),
	thread_send_message(Caller, '$trace'(Result)).

%%	in_debug_thread(+Thread, :Goal) is semidet.
%%	in_debug_thread(+Object, :Goal) is semidet.
%
%	Run Goal in the thread  being   debugged.  The first argument is
%	either an XPCE object that is part  of the debugger window, or a
%	thread identifier.

in_debug_thread(Object, Goal) :-
	object(Object), !,
	get(Object, frame, Frame),
	get(Frame, thread, Thread),
	in_debug_thread(Thread, Goal).
in_debug_thread(Thread, Goal) :-
	thread_self(Thread), !,
	Goal, !.
in_debug_thread(Thread, Goal) :-
	strip_module(Goal, M, G),
	thread_self(Self),
	debug('Call [Thread ~p] ~p~n', [Thread, M:G]),
	term_variables(G, GVars),
	thread_send_message(Thread, '$trace'(call(M:G, GVars, Self))),
	thread_get_message('$trace'(Result)),
	debug(' ---> in_debug_thread: result = ~p~n', [Result]),
	(   Result = error(E)
	->  throw(E)
	;   Result = true(BGVars)
	->  GVars = BGVars
	).


%%	prolog_frame_attribute(+GUI, +Frame, +Attribute, -Value) is det.
%%	prolog_frame_attribute(+Thread, +Frame, +Attribute, -Value) is det.
%
%	As prolog_frame_attribute/3, but calling in the thread debugged
%	by GUI.

prolog_frame_attribute(Thread, Frame, Attribute, Value) :-
	in_debug_thread(Thread,
			prolog_frame_attribute(Frame, Attribute, Value)).


		 /*******************************
		 *     DEBUGGER APPLICATION	*
		 *******************************/

:- pce_global(@prolog_gui, new(prolog_gui)).

:- pce_begin_class(prolog_gui, application,
		   "Toplevel driver for the Prolog GUI").

initialise(App) :->
	send_super(App, initialise, 'Prolog Debugger GUI'),
	send(App, kind, service).	% Do not debug in this

:- pce_end_class.


		 /*******************************
		 *	   DEBUGGER FRAME	*
		 *******************************/

:- pce_begin_class(prolog_debugger, persistent_frame,
		   "Toplevel driver for the debugger").

variable(source,	any,		both, "Source view").
variable(break_level,	int,		get,  "Break-level I'm associated to").
variable(thread,	'int|name*',	get,  "Associated thread").
variable(current_frame, int*,   	both, "The most recent frame").
variable(current_break,	tuple*,		both, "tuple(ClauseRef, PC)").
variable(quitted,	bool := @off,   both, "Asked to quit").
variable(mode,		name := created,both, "Current mode").

initialise(F, Level:int, Thread0:'int|name') :->
	assertion(thread_self(main)),
	default(Thread0, main, Thread),
	send(F, slot, break_level, Level),
	send(F, slot, thread, Thread),
	send_super(F, initialise, 'SWI-Prolog debugger', application := @prolog_gui),
	send(F, icon, resource(debug)),
	send(F, done_message, message(F, quit)),
	send(F, append, new(MBD, dialog)),
	send(MBD, gap, size(0, 2)),
	send(MBD, pen, 0),
	send(MBD, append, new(menu_bar)),
	send(MBD, name, menu_bar_dialog),
	send(MBD, resize_message, message(MBD, layout, @arg2)),
	send(F, fill_menu_bar),
	send(new(D, prolog_button_dialog), below, MBD),
	send(D, name, buttons),

	new(V, prolog_bindings_view),
	send(V, label, 'Bindings'),
	send(V, name, bindings),
	send(new(S, prolog_stack_view), right, V),
	send(V, below, D),
	send(new(Src, prolog_source_view), below, V),
	send(F, source, Src),
	send(new(RD, report_dialog), below, Src),
	send(RD, warning_delay, 0),
	send(S, label, 'Call Stack'),
	send(S, name, stack),
	ignore(send(F, frame_finished, 0)),	% FR_WATCHED issue
	asserta(gui(Thread, Level, F)).

unlink(F) :->
	retractall(gui(_, _, F)),
	clear_clause_info_cache,	% safety first
	send_super(F, unlink).

quit(F) :->
	"User initiated quit"::
	(   (   get(F, mode, thread_finished)
	    ;   get(F, mode, query_finished)
	    ;   get(F, mode, aborted)
	    )
	->  send(F, destroy)
	;   get(F, tracer_quitted, Action),
	    (   Action == cancel
	    ->  true
	    ;   send(F, return, Action)
	    )
	).

label(F, Label:char_array) :->
	"Set label, indicating associated thread"::
	get(F, thread, Thread),
	(   Thread == main
	->  send_super(F, label, Label)
	;   send_super(F, label, string('[Thread %s] %s', Thread, Label))
	).

clear_stack_window(F) :->
	"Clear the stack window"::
	get(F, member, stack, StackView),
	send(StackView, clear).

clear(F, Content:[bool]) :->
	"Deactivate all views"::
	ignore(send(F, send_hyper, fragment, free)),
	get(F, member, stack, StackView),
	send(StackView, clear),
	get(F, member, bindings, BindingView),
	send(BindingView, clear, Content).


fill_menu_bar(F) :->
	get(F, member, menu_bar_dialog, MBD),
	get(MBD, member, menu_bar, MB),
	send(MB, append, new(Tool, popup(tool))),
	send(MB, append, new(Edit, popup(edit))),
	send(MB, append, new(View, popup(view))),
	send(MB, append, new(Comp, popup(compile))),
	send(MB, append, new(Help, popup(help)), right),
	send_list(Tool, append,
		  [ menu_item(settings,
			      message(F, settings),
			      end_group := @on),
		    menu_item(clear_source_cache,
			      message(@prolog, clear_clause_info_cache),
			      end_group := @on),
		    menu_item(quit,
			      message(F, quit))
		  ]),
	send_list(Edit, append,
		  [ menu_item(breakpoints,
			      message(F, breakpoints)),
		    menu_item(exceptions,
			      message(F, exceptions),
			      end_group := @on),
		    menu_item(toggle_edit_mode,
			      message(F, edit))
		  ]),
	send_list(View, append,
		  [ menu_item(threads,
			      message(F, show_threads))
		  ]),
	send_list(Comp, append,
		  [ menu_item(make,
			      message(F, make),
			      end_group := @on)
		  ]),
	send_list(Help, append,
		  [ menu_item(about,
			      message(F, about)),
		    menu_item(help_on_debugger,
			      message(F, help),
			      end_group := @on),
		    menu_item(prolog_manual,
			      message(@prolog, prolog_help)),
		    menu_item('XPCE manual',
			      message(@prolog, manpce))
		  ]).

settings(_F) :->
	"Edit the preferences"::
	trace_settings.


about(_) :->
	"Display aout message"::
	version(Version),
	send(@display, inform,
	     'SWI-Prolog debugger version %s\n\
	      By Jan Wielemaker',
	     Version).

help(_) :->
        "Show window with help-text"::
        send(@helper, give_help, pltracer, main).

show_frame(GUI, Frame:int, PC:'int|name') :->
	"Show the variables of this frame"::
	prolog_show_frame(Frame, [gui(GUI), pc(PC), source, bindings]).

show_stack(GUI, CallFrames:prolog, ChoiceFrames:prolog) :->
	"Show the stack and choicepoints"::
	get(GUI, member, stack, StackWindow),
	send(StackWindow, clear),
	display_stack(StackWindow, CallFrames, ChoiceFrames).

show_threads(_GUI) :->
	"Open Thread monitor"::
	prolog_ide(thread_monitor).


		 /*******************************
		 *	      EVENT		*
		 *******************************/

source_typed(Frame, Typed:event_id) :->
	"Forward a typing event to the button-dialog"::
	get(Frame, member, buttons, Dialog),
	send(Dialog, typed, Typed).


		 /*******************************
		 *	     ACTIONS		*
		 *******************************/

window_pos_for_button(F, ButtonName:name, Pos:point) :<-
	"Return position for transient window reacting on Button"::
	get(F, member, buttons, Dialog),
	get(Dialog, button, ButtonName, Button),
	get(Button, display_position, ButtonPos),
	get(ButtonPos, plus, point(0, 25), Pos).

prepare_action(Frame) :->
	"Prepare for reading an action"::
	send(Frame, open),		% make sure
	get(Frame, display, Display),
	send(Display, busy_cursor, @nil),
	send(Display, synchronise),
	send(Frame, mode, wait_user).

action(Frame, Action:name) :<-
	"Wait for the user to return an action"::
	send(Frame, prepare_action),
	get(Frame, confirm, Action),
	(   get(Frame, quitted, @on)
	->  send(Frame, destroy)
	;   true
	).

return(Frame, Result:any) :->
	"Return user action"::
	(   get(Frame, mode, wait_user)
	->  get(Frame, thread, Thread),
	    send(Frame, mode, replied),
	    (   Thread == main
	    ->  send_super(Frame, return, Result)
	    ;   (   get(Frame, quitted, @on)
		->  send(Frame, destroy)
		;   true
		),
		debug(' ---> frame: result = ~p~n', [Result]),
		thread_send_message(Thread, '$trace'(action(Result)))
	    )
	;   get(Frame, quitted, @on)
	->  send(Frame, destroy)
	;   send(Frame, report, warning, 'Not waiting')
	).

%%	tracer_quitted(+Thread,	-Action) is semidet.
%
%	Ask the user what to  do  after   a  user-initiated  quit of the
%	debugger.

tracer_quitted(Frame, Action) :<-
	"Confirm user requested quit"::
	get(Frame, thread, Thread),
	(   Thread == main
	->  Label = 'Tracer quitted'
	;   Label = string('[Thread %s] Tracer quitted', Thread)
	),
	new(D, dialog(Label)),
	send(D, application, @prolog_gui),
	send(D, append,
	     button(continue_without_debugging,
		    message(D, return, nodebug))),
	send(D, append,
	     button(abort,
		    message(D, return, abort))),
	(   Thread == main
	->  send(D, append,
		 button(exit_prolog,
			message(D, return, halt)))
	;   true
	),
	send(D, append,
	     button(cancel,
		    message(D, return, cancel))),
	send(D, transient_for, Frame),
	send(D, modal, transient),
	get(D, confirm_centered, Frame?area?center, Action),
	send(D, destroy),
	(   Action == cancel
	->  true
	;   send(Frame, quitted, @on)
	).


selected_frame(F, Frame:int) :<-
	"Prolog frame selected by user in stack window"::
	get(F, member, stack, Browser),
	get(Browser, selection, Frame).


		 /*******************************
		 *  ACTIONS OF THE SOURCE VIEW	*
		 *******************************/

:- pce_group(actions).

edit(F) :->
	"(Toggle) Edit-mode of source-window"::
	send(F?source, edit).

breakpoints(_F) :->
	"Edit spy/break/trace-points"::
	prolog_ide(open_debug_status).

exceptions(_F) :->
	"Edit exceptions"::
	prolog_ide(open_exceptions).

make(_) :->
	"Run Prolog make"::
	(   object(@emacs)
	->  send(@emacs, save_some_buffers)
	;   true
	),
	make.

goal(F, Goal:prolog) :<-
	"Return qualitied term for selected frame"::
	get(F, selected_frame, Frame),
	prolog_frame_attribute(F, Frame, goal, Goal0),
	(   Goal0 = _:_
	->  Goal = Goal0
	;   Goal = user:Goal0
	).

nostop_or_spy(F) :->
	"Clear spy-point"::
	(   send(F?source, delete_selected_stop)
	->  true
	;   get(F, current_break, tuple(ClauseRef, PC))
	->  '$break_at'(ClauseRef, PC, false)
	;   (   get(F, current_frame, Frame)
	    ;   get(F, selected_frame, Frame)
	    ),
	    Frame \== @nil,
	    prolog_frame_attribute(F, Frame, goal, Goal0),
	    (   Goal0 = _:_
	    ->  Goal = Goal0
	    ;   Goal = user:Goal0
	    ),
	    '$get_predicate_attribute'(Goal, spy, 1)
	->  nospy(Goal)
	;   send(F, report, warning,
		 'No selected break or current spy-point')
	).

browse(_F) :->
	"Provides overview for edit/spy/break"::
	prolog_ide(open_navigator).

stop_at(F) :->
	"Set stop at caret"::
	get(F, source, SourceWindow),
	send(SourceWindow, stop_at).

up(F) :->
	"Select child frame"::
	get(F, member, stack, Stack),
	send(Stack, up).

down(F) :->
	"Select parent frame"::
	get(F, member, stack, Stack),
	send(Stack, down).

details(F) :->
	"Show (variable) details"::
	get(F, member, bindings, Bindings),
	send(Bindings, details).

query(_F) :->
	"Enter and run a query"::
	prolog_ide(open_query_window).

:- pce_group(delegate).

file(F, File:'name|emacs_buffer*') :->
	"Attach to indicated file"::
	send(F?source, source, File).

show_range(F, File:'name|text_buffer', From:int, To:int, Style:name) :->
	"Show indicated region using Style"::
	send(F?source, show_range, File, From, To, Style).

show_line(F, File:'name|text_buffer', Line:int, Style:name) :->
	"Show numbered line"::
	send(F?source, show_line, File, Line, Style).

listing(F, Module:name, Predicate:name, Arity:int) :->
	"List the specified predicate"::
	send(F?source, listing, Module, Predicate, Arity).

/* NOTE: lazy creation of this message interferes with call_cleanup/2 used
   by findall/3 from XPCE's lazy  method   binder.  Therefore  we make a
   dummy call to this method in ->initialise.
*/

frame_finished(F, Frame:int) :->
	"This frame was terminated; remove it"::
	get(F, member, stack, StackView),
	send(StackView, frame_finished, Frame),
	(   get(F, member, bindings, Bindings),
	    get(Bindings, prolog_frame, Frame)
	->  send(Bindings, background, grey80),
	    send(Bindings, slot, prolog_frame, @nil),
	    ignore(send(F, send_hyper, fragment, free))
	;   true
	),
	(   get(F, current_frame, Frame)
	->  send(F, current_frame, @nil)
	;   true
	).

aborted(F) :->
	"User has aborted the query"::
	send(F, clear, @off),
	send(F, mode, aborted),
	send(F, report, status, 'Execution aborted').

thread_finished(F, Status:prolog) :->
	"Thread I'm associated with finished"::
	send(F, clear),
	send(F, mode, thread_finished),
	format(string(String), '~q', Status),
	send(F, report, status, 'Thread finished: %s', String).

query_finished(F, Message:char_array) :->
	"Toplevel query finished"::
	send(F, clear),
	send(F, mode, query_finished),
	send(F, report, status, Message).

:- pce_end_class(prolog_debugger).

		 /*******************************
		 *	      BUTTONS		*
		 *******************************/

:- pce_begin_class(prolog_button_dialog, dialog,
		   "Dialog holding the function buttons").

%	button(Action, Keys, Image, Balloon)
%
%	If action is +Action, send message Action to the frame.  Otherwise
%	return Action to the caller.

button(into,	       "i",   'into.xpm',	     'Show unification').
button(creep,	       "\n ", 'creep.xpm',	     'Step').
button(skip,	       "s",   'skip.xpm',	     'Skip over this goal').
button(finish,	       "f",   'finish.xpm',	     'Finish selected goal').
button(gap,	       -,     -,		     -).
button(retry,	       "r",   'retry.xpm',	     'Retry selected goal').
button(gap,	       -,     -,		     -).
button(nodebug,	       "n",   'nodebug.xpm',	     'Continue without debugging').
button(+query,	       "b",   'break.xpm',	     'Enter a query').
button(abort,	       "a",   'abort.xpm',	     'Abort to the Prolog toplevel').
button(fail,	       "F",   'fail.xpm',	     'Force query to fail').
button(gap,	       -,     -,		     -).
button(+up,	       "u",   'up.xpm',		     'Select parent frame').
button(+down,	       "d",   'down.xpm',	     'Select child frame').
button(gap,	       -,     -,		     -).
button(+browse,	       "",    '16x16/butterfly.xpm', 'Browse program structure').
button(gap,	       -,     -,		     -).
button(leap,	       "l",   'leap.xpm',	     'Continue to spy- or breakpoint').
button(+breakpoints,   "+",   'spy.xpm',	     'Edit spy- and breakpoints').
button(+stop_at,       "!",   'stop.xpm',	     'Set Stop at caret').
button(+nostop_or_spy, "-",   'nostopspy.xpm',	     'Delete break- or spy-point').
button(gap,	       -,     -,		     -).
button(+details,       "v",   'details.xpm',	     'Show (variable) details').
button(+edit,	       "e",   'edit.xpm',	     'Toggle read-only/edit-mode').


tag_balloon(Balloon0, Keys, Balloon) :-
	maplist(key_name, Keys, Names),
	atomic_list_concat(Names, ', ', Tag),
	atomic_list_concat([Balloon0, ' (', Tag, ')'], Balloon).

key_name(10, return) :- !.
key_name(32,  space) :- !.
key_name(C, A) :-
	char_code(A, C).

initialise(D) :->
	send_super(D, initialise),
	send(D, pen, 0),
	send(D, gap, size(0,0)),
	get(D, frame, Frame),
	send(D, append, new(TB, tool_bar(Frame))),
	(   button(Action, Keys, Image, Balloon0),
	    (	Action == gap
	    ->	send(TB, append, gap)
	    ;   tag_balloon(Balloon0, Keys, Balloon),
		make_message(Action, D, Message),
	        send(TB, append,
		     new(B, tool_button(Message,
					image(Image),
					Balloon))),
	        chain_list(KL, Keys),
	        send(B, attribute, keys, KL)
	    ),
	    fail
	;   true
	).

make_message(+Action, D, message(D?frame, Action)) :- !.
make_message(Action,  D, message(D, return, Action)).

typed(D, Id:event_id, Delegate:[bool]) :->
	"Handle typing"::
	(   get(D, find, @default,
		and(message(@arg1, has_get_method, keys),
		    message(@arg1?keys, member, Id)),
		Button)
	->  send(Button, execute)
	;   Delegate == @on
	->  send_super(D, typed, Id, Delegate)
	).

event(D, Ev:event) :->
	(   send(Ev, is_a, keyboard)
	->  send(D, typed, Ev)
	;   send_super(D, event, Ev)
	).

button(D, Name:name, Button:button) :<-
	"Find button from its name"::
	get(D, member, tool_bar, TB),
	get(TB, member, Name, Button).

:- pce_end_class(prolog_button_dialog).


		 /*******************************
		 *	     VARIABLES		*
		 *******************************/

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
We use a view with some tweaks   to  display the bindings. Originally we
used a browser, but a view has  two   advantages.  First  of all, we can
write directly to it by opening it as   a stream and second the user can
use search and selection on the view to   analyse it or export text from
it.
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

:- pce_begin_class(prolog_bindings_view, view,
		   "Overview of bindings of the current frame").

class_variable(font,	font, 	normal, "Font for bindings").
class_variable(size,    size,   size(40,11), "Initial size").

variable(prolog_frame, int*, both, "Frame who's variables we are showing").

:- pce_global(@prolog_binding_recogniser,
	      make_prolog_binding_recogniser).

make_prolog_binding_recogniser(G) :-
	new(View, @event?window),
	new(Index, ?(@event?receiver, index, @event)),
	new(C1, click_gesture(left, '', single,
			      message(View, on_click, Index))),
	new(C2, click_gesture(left, '', double,
			      message(View, details))),
	new(G, handler_group(C1, C2)).


initialise(B) :->
	send_super(B, initialise),
	send(B?text_buffer, undo_buffer_size, 0),
	get(B, font, Font),
	get(Font, ex, Ex),
	Tab is 15 * Ex,
	send(B, wrap, none),
	send(B?image, tab_stops, vector(Tab)),
	send(B?image, recogniser, @prolog_binding_recogniser),
	send(B, editable, @off),
	send(B?text_cursor, displayed, @off),
	send(B, ver_stretch, 0).

clear(B, Content:[bool]) :->
	send(B, prolog_frame, @nil),
	(   Content == @off
	->  send(B, background, grey80)
	;   send_super(B, clear)
	).

details(B, Fragment:[prolog_frame_var_fragment]) :->
	"View details of the binding"::
	get(B, prolog_frame, Frame),
	(   Frame \== @nil
	->  true
	;   send(B, report, warning, 'No current frame'),
	    fail
	),
	(   Fragment == @default
	->  (   get(B, selected_fragment, Frag),
	        Frag \== @nil
	    ->	true
	    ;	send(B, report, warning, 'No selected variable'),
		fail
	    )
	;   Frag = Fragment
	),
	get(Frag, var_name, VarName),
	get(Frag, value, Value),
	prolog_frame_attribute(B, Frame, level, Level),
	prolog_frame_attribute(B, Frame, goal, Goal),
	predicate_name(Goal, PredName),
	(   integer(VarName)
	->  VarType = 'Argument'
	;   VarType = 'Variable'
	),
	format(string(Label), '~w ~w of frame at level ~d running ~w',
		[ VarType, VarName, Level, PredName ]),
	view_term(Value,
		  [ comment(Label),
		    source_object(Frag),
		    expose(true)
		  ]).

on_click(B, Index:int) :->
	"Select fragment clicked"::
	get(B, text_buffer, TB),
	send(B, selection, 0, 0),
	(   get(TB, find_fragment, message(@arg1, overlap, Index), Frag)
	->  send(B, selected_fragment, Frag)
	;   send(B, selected_fragment, @nil)
	).

bindings(B, Bindings:prolog) :->
	"Display complete list of bindings"::
	send(B, background, white),
	pce_open(B, write, Fd),
	forall(member(Vars=Value, Bindings),
	       send(B, append_binding, Vars, value(Value), Fd)),
	close(Fd),
	send(B, caret, 0).

append_binding(B, Names:prolog, ValueTerm:prolog, Fd:prolog) :->
	"Add a binding to the browser"::
	ValueTerm = value(Value),	% protect :=, ?, etc.
	(   var(Value),
	    setting(show_unbound, false)
	->  true
	;   get(B, text_buffer, TB),
	    get(TB, size, S0),
	    (   Names = VarName:ArgN
	    ->	format(Fd, '~w', [VarName])
	    ;	Names = [VarName:ArgN|_],
	        write_varnames(Fd, Names)
	    ),
	    setting(attributes, Atts),
	    current_prolog_flag(toplevel_print_options, Options0),
	    delete(Options0, attributes(_), Options1),
	    format(Fd, '\t= ~W~n', [Value, [attributes(Atts)|Options1]]),
	    flush_output(Fd),
	    get(TB, size, S1),
	    new(_, prolog_frame_var_fragment(TB, S0, S1, VarName, ArgN))
	).

write_varnames(Fd, [N:_]) :- !,
	format(Fd, '~w', N).
write_varnames(Fd, [N:_|T]) :-
	format(Fd, '~w = ', N),
	write_varnames(Fd, T).

:- pce_end_class(prolog_bindings_view).


:- pce_begin_class(prolog_frame_var_fragment, fragment,
		   "Represent a variable in a frame").

variable(var_name, name, get, "Name of displayed variable").
variable(argn,	   int,  get, "Slot in frame").

initialise(F, TB:text_buffer, From:int, To:int, Name:name, ArgN:int) :->
	Len is To-From,
	send_super(F, initialise, TB, From, Len, frame),
	send(F, slot, var_name, Name),
	send(F, slot, argn, ArgN).

%	Issue: this copies really big values

value(F, Value:prolog) :<-
	"Get current value of the variable"::
	get(F, text_buffer, TB),
	get(TB?editors, head, Editor),
	get(Editor, window, View),
	get(View, prolog_frame, Frame), Frame \== @nil,
	get(F, argn, ArgN),
	prolog_frame_attribute(F, Frame, argument(ArgN), Value).

:- pce_end_class(prolog_frame_var_fragment).


		 /*******************************
		 *	       EVENTS		*
		 *******************************/

%user:prolog_event_hook(Term) :-
%	debug('prolog_event_hook(~w).~n', [Term]),
%	fail.
user:prolog_event_hook(frame_finished(Frame)) :-
	thread_self(Thread),
	gui(Thread, _, Gui),
	send(Gui, frame_finished, Frame),
	fail.
user:prolog_event_hook(exit_break(Level)) :-
	thread_self(Thread),
	gui(Thread, Level, Gui),
	send(Gui, destroy),
	fail.
user:prolog_event_hook(finished_query(_Qid, YesNo)) :-
	thread_self(Thread),		% only main?
	break_level(Level),
	gui(Thread, Level, Ref),
	send(Ref, query_finished, YesNo),
	fail.
user:prolog_event_hook(thread_finished(Thread)) :-
	gui(Thread, _, Gui),
	thread_property(Thread, status(Status)),
	in_pce_thread(send(Gui, thread_finished, Status)),
	fail.

user:message_hook('$aborted', _, _Lines) :-
	aborted,
	fail.
user:message_hook(query(YesNo), _, _Lines) :-
	query_finished(YesNo),
	fail.

aborted :-
	thread_self(Thread),
	gui(Thread, Level, Gui),
	(   Level \== 0
	->  send(Gui, destroy)
	;   send(Gui, aborted)
	).

query_finished(YesNo) :-
	finished(YesNo, Message),
	thread_self(Thread),
	break_level(Level),
	gui(Thread, Level, Gui),
	send(Gui, query_finished, Message).

finished(no, 'Query failed').
finished(yes, 'Query succeeded').
finished(done, 'User ended query').
finished(yes(_), 'Query succeeded with result').
finished(more(_), 'Query succeeded non-deterministically with result').
