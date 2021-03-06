			The Common Prolog Library

README Last update: Aug 14, 2008

This directory contains libraries that are   shared verbatim between YAP
and SWI-Prolog. The library is managed using GIT. The SWI branch of this
library is hosted at:

    git://gollem.science.uva.nl/home/pl/lib.git


The YAP/SWI compatibility layer
-------------------------------

The YAP and SWI teams have  agreed   to  implement some basic facilities
that enable you to load the  same   Prolog  file in both systems despite
existing differences in built-ins and libraries.   See this link for the
description in the SWI-Prolog manuals   (appendix C, "Compatibility with
other Prolog dialects"):

    http://hcs.science.uva.nl/projects/SWI-Prolog/Manual/dialect.html


Adding files to this library (SWI)
----------------------------------

This  library  creates   $PLBASE/library/common    in   the   SWI-Prolog
installation. Files in it can be  loaded using library(file), where this
library is searched *after* the system library.
