# NeoKappa

## example

```
%agent: N(e3,e5,o,i,s~A~T~G~C~U~AP)

%compartment: foo sphere
%location: bar [1][2][3]


#Grow rules
'growAT' N : foo(i:bar !0,e5),N(i!0,e3) <-> N(i!0,e5!1),N(i!0,e3!2),N(e3!1,i!3,s~A),N(e5!2,i!3,s~T)
'growTA' N(i!0,e5),N(i!0,e3) -> N(i!0,e5!1),N(i!0,e3!2),N(e3!1,i!3,s~T),N(e5!2,i!3,s~A) @ 'kAT'
'growGC' N(i!0,e5),N(i!0,e3) -> N(i!0,e5!1),N(i!0,e3!2),N(e3!1,i!3,s~G),N(e5!2,i!3,s~C) @ 'kGC'
'growCG' N(i!0,e5),N(i!0,e3) -> N(i!0,e5!1),N(i!0,e3!2),N(e3!1,i!3,s~C),N(e5!2,i!3,s~G) @ 'kGC'

#seeds

'seedAT' -> N(i!0,s~A),N(i!0,s~T)  @ 'sAT'
'seedGC' -> N(i!0,s~G),N(i!0,s~C)  @ 'sGC'

%var: 'kGC' 10
%var: 'kAT' 1.5 * 'kGC'
%var: 'sGC' 1
%var: 'sAT' 1.5 * 'sGC'

%init: 'sAT' N()
```

