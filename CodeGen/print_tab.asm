
'[' : 91
']' : 93
' ' : 32

print_tab:                          // *print_tab
	SUBi SP SP 1
	LD R1 SP		                # profondeur
	SUBi SP SP 1
	LD R2 SP		                # pointeur du tableau
	LD R3 R2		                # longueur du tableau
	XOR R4 R4 R4
	ADDi R4 R4 91
	OUT R4			                # on affiche "["
	SUBi R4 R4 59
	OUT R4			                # on affiche " "
	XOR R5 R5 R5	                # i = 0
	ADDi R2 R2 1	                # on pointe maintenant sur le premier élément
	ADDi SP SP 2	                # on bouge le SP tout en haut
debut_boucle_print_tab:             // *loop_start
	JEQU R5 R3 fin_pour_print_tab
	XOR R6 R6 R6
	MODi R7 R5 10
	JNEQ R7 R6 skip_tab_end	# i % 10 != 0
	JEQU R5 R6 skip_tab_end	# i == 0
	LD R2 R2
skip_tab_end:                       //*skip_tab_end
	XOR R9 R9 R9
	ADDi R9 R9 1
	JEQU R1 R9 print_element
	ST R5 SP						# on empile i pour le retrouver plus tard
	ADDi SP SP 1					
	ST R3 SP						# on empile la longueur du tableau pour la retrouver plus tard
	ADDi SP SP 1
	ST R2 SP						# on empile le pointeur du tableau pour le retrouver plus tard
	ADDi SP SP 1
	LD R2 R2
	ST R2 SP						# on empile le sous tableau pour l'afficher
	ADDi SP SP 1
	SUBi R11 R1 1					# R11 := profondeur - 1
	ST R11 SP						# on empile profondeur - 1
	ADDi SP SP 1
	CALL print_tab
	ADDi SP SP 1
	LD R1 SP						# on dépile profondeur - 1
	ADDi R1 R1 1					# profondeur++
	SUBi SP SP 2
	LD R2 SP						# on dépile le pointeur du tableau
	SUBi SP SP 1
	LD R3 SP						# on dépile la taille du tableau
	SUBi SP SP 1
	LD R5 SP						# on dépile i
	JMP fin_print_element
print_element:                      // *print_elem
	LD R12 R2
	PRINT R12
fin_print_element:                  // *print_elem_end
	XOR R8 R8 R8
	ADDi R8 R8 32
	OUT R8			                # on affiche " "
	ADDi R2 R2 1	                # on pointe maintenant sur le prochain élément
	ADDi R5 R5 1	                # i++
	JMP debut_boucle_print_tab
fin_pour_print_tab:                 // *loop_end
	XOR R4 R4 R4
	ADDi R4 R4 93
	OUT R4 			                # on affiche "]"
	SUBi SP SP 2	                # on rebouge le SP en bas
	RET
