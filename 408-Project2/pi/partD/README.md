./pipair.sh <*.bc>
Optional: <t_support> + <t_confidence>
Followed by 
Optional: <t_distance>
Followed by
Optional: <”ignore”> + <function1> + <function2> ...

Part D includes two extra inputs that the user can enter before running our bug-detection program. 
The first is an integer representing the distance to threshold, which will allow users to check for bugs 
involving functions that are called in direct succession in the callgraph. The second is a list of functions 
in the call graph that the user would like to be excluded from the bug detection, such as strcmp() or scanf(). 
These inputs must be entered in the correct order, distance to threshold first and list of functions following 
that. One or both of these inputs can remain empty. 
