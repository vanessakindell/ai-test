Version#=1.161

AppTitle "Stage 1 Artificial Intelligence: Learning dictionary v"+Version#,"Don't close with the X! Do it anyway?"
Graphics 800,600,16,2
SetBuffer(FrontBuffer())
SeedRnd(MilliSecs())

; Types
Type Knowledge
	Field Trigger$,Info$,Strength#,Source$
End Type

Type scriptentry
	Field sdat$
End Type

Type like
	Field word$,Opinion#
End Type

Type noun
	Field name$
End Type

Global Response$
Global OldReply$
Global OldResponse$
Global Know
Global Reply$="Hello "+GetEnv("USERNAME")+"."
Global Forget$
Global Rage#=0
Global Voice=True
Global Logged=True

If Instr(CommandLine$(),"-nospeech")<>0 Then Voice=False
If Instr(CommandLine$(),"-nolog")<>0 Then Logged=False

; Load Database
If FileType("database.dat")=1 Then
	ReadDatabase()
EndIf
If FileType("nouns.txt")=1 Then
	LoadNouns()
EndIf
If FileType("mood.dat")=1 Then
	MoodFile=ReadFile("mood.dat")
	Rage#=ReadFloat(MoodFile)
	Mood$=ReadFloat(MoodFile)
	CloseFile MoodFile
EndIf

; Launch communication
Communicate()

; Functions
Function Communicate()
	; Start
	Cls
	Color 255,255,0
	Print "For this to work you will have to have a conversation as a tutor."
	Print "If you ask a question, you must reply with either yes or the correct answer."
	Print "To encourage the AI to forget something, reply with forget that."
	Print "It has a single dimensional mood."
	Print "You can say exit to exit."
	Print "Note: There may be a noticable delay while it checks memory."
	
	If FileType("Logs")<>2 Then
		CreateDir("Logs")
	EndIf
	If logged Then
		ChatLog=WriteFile("Logs/"+CurrentDate()+" "+MilliSecs()+".txt")
		WriteLine ChatLog,Reply$
	EndIf
	
	While Not Lower(Response$)="exit" Or Lower(Response$)="quit"
		Color 255,255,255
		Select Rage#:
			Case -5
				Mood$="relaxed"
			Case -4:
				Mood$="chill"
			Case -3:
				Mood$="happy"
			Case -2:
				Mood$="good"
			Case -1,0,1
				Mood$="ok"
			Case 2
				Mood$="stoked"
			Case 3:
				Mood$="allert"
			Case 4:
				Mood$="annoyed"
			Case 5:
				Mood$="uptight"
			Case 6:
				Mood$="mad"
			Case 7:
				Mood$="angry"
			Case 8:
				Mood$="irate"
			Case 9:
				Mood$="fuming"
			Default
				If Rage#<-5 Then Mood$="tranquil"
				If Rage#>9 Then Mood$="steaming"
		End Select
		If Rage#<-1 Then
			Print Lower(Reply$)
		Else If Rage#>1 Then
			Print Upper(Reply$)
		Else
			Print Reply$
		EndIf
		If Voice=True Then
			ExecFile "espeak -v  en "+Chr(34)+Reply$+Chr(34)
		EndIf
		ReallyOldReply$=OldReply$
		OldReply$=Reply$
		OldResponse$=Response$
		OldReply$=Replace$(OldReply$,", I think.","")
		Color 0,0,255
		.inp
		Response$=Input(">")
		If Response$="" Then Goto inp
		Color 255,255,255
		DoAI()
		If Rage#=50 And raged=False Then
			Reply$="I'LL NEVER GET THIS!"
			Raged=True
		EndIf
		If Rage#=100 Then
			Print "I just give up!"
				If Instr(CommandLine$(),"-nospeech")=0 Then
					ExecFile "espeak -v  en "+Chr(34)+"I just give up!"+Chr(34)
				EndIf
			SaveData()
			SaveMood()
			SaveNouns()
			If logged Then
				WriteLine ChatLog,"The AI ragequit."
				CloseFile ChatLog
			EndIf
			RuntimeError "The AI ragequit."
		EndIf
		If Upper(Response$)=Response$ Then
			Rage#=Rage#+1
		EndIf
		If Lower(Response$)=Response$ Then
			Rage#=Rage#-1
		EndIf
		For L.Like=Each Like
			For Lk#=1 To Len(dequestion(Response$))
				If Lower(Mid(dequestion(Response$),Lk#,Len(L\Word$)))=Lower(L\Word$) Then
					Rage#=Rage#+(L\Opinion#-5)
				EndIf
			Next
		Next
		If Lower(Response$)<>"exit" Then
			Know=False
				Select Lower(Response$)
						Case "cls"
							Cls
							Locate 0,0
							Know=True
						Case "forget that":
							Rage#=Rage#+1
							Forget(Forget$)
							Forget(Reply$)
							Reply$="Ok"
							Know=True
						Case "what is the date?
							Replay$="It is "+CurrentDate$()
							Know=True
						Case "what time is it?"
							Reply$="It is "+CurrentTime$()
							Know=True
						Case "clear"
							Cls
							Locate 0,0
							Know=True
						Case "hello pupil"
							Random()
							Know=True
						Case "hello student"
							Random()
							Know=True
						Case "knock knock"
							Reply$="Who's there?"
							Know=True
						Case "how do you feel?"
							Reply$="I feel "+Mood$+"."
							Know=True
						Case "how are you?"
							Reply$="I'm "+Mood$+"."
							Know=True
						Case "who are you?"
							Reply$="I am a test AI"
							Know=True
						Case "how are you feeling?"
							Reply$="I'm feeling "+Mood$+"."
							Know=True
						Case "no":
							Rage#=Rage#+1
							ForgetSingle(OldResponse$,OldReply$)
							Know=True
						Case "disable voice"
							Voice=False
							Know=True
						Case "enable voice"
							Voice=True
							Know=True
						Case "yes":
							Rage#=Rage#-1
							Memorize(OldResponse$,OldReply$,"yes")
							Know=True
				End Select
			If Know=False Then Know=CheckMemory()
			If Know=False Then
				If Instr(OldReply$,"?")<>0 And Instr(Response$,"?")=0 And Lower(Response$)<>"forget that" And Instr(OldRespons$,"?")=0 And Instr(Lower(OldReply$),"could it be ")=0 Then
					If Lower(OldReply$)="who's there?" Then
						Reply$=Response$+" who?"
					Else
						If Lower(Response$)<>"no" Then
							If Lower(Response$)="yes" Then
								OldReply$=Replace$(Lower(OldReply$),"could it be ","")
								OldReply$=Replace$(Lower(OldReply$),"is it ","")
								OldReply$=Replace$(Lower(OldReply$),", i think.","")
								Memorize(ReallyOldReply$,OldReply$,"AIQ yes")
							Else
								OldReply$=Replace$(Lower(OldReply$),"could it be ","")
								OldReply$=Replace$(Lower(OldReply$),"is it ","")
								OldReply$=Replace$(Lower(OldReply$),", i think.","")
								Memorize(OldReply$,Response$,"AIQ")
							EndIf
						EndIf
					EndIf
				Else
					If Lower(Dequestion(Response$))="your name" Then
						Reply$="John"
					Else If Instr(OldResponse$,"?")<>0 Then
						If Instr(Response$,"?")=0 And Lower(Response$)<>"no" And Lower(Response$)<>"yes" And Instr(Lower(Response$),"tell me")=0 Then
							Memorize(OldResponse$,Response$,"learn")
						EndIf
					Else
						If Instr(Lower(Response$),"tell me")=0 Then
							If Reply$="Ok" Then
								Memorize("none",Response$,"norm")
							Else
								If Instr(Response$,"?")=0 Then
									ReplyVague()
									Memorize(Reply$,Response$,"known")
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
			If Instr(Lower(Response$),"i ")<>0 And Instr(Lower(Response$),"?")=0 And Reply$="Ok" Then
				Memorize("none",Replace$(Lower(Response$),"i ","you "),"you")
				Reply$="Interesting"
			EndIf
		EndIf
		If Instr(OldResponse$,"?")<>0 And OldReply$="I don't know." Then
			If Lower(Response$)<>"exit" Then Memorize(OldResponse$,Response$,"norm Q")
		EndIf
		If logged Then
			WriteLine ChatLog,Response$
			WriteLine ChatLog,Reply$
		EndIf
	Wend
	SaveData()
	SaveNouns()
	SaveMood()
	If logged Then CloseFile ChatLog
	End
End Function

Function SaveData()
	; Save Data
	
	;make sure there is memory to save
	Memories=0
	For K.Knowledge = Each Knowledge
		Memories=Memories+1
	Next
	If Memories>0 Then
		Database=WriteFile("database.dat")
		WriteLine Database,"@database("
		For K.Knowledge = Each Knowledge
			WriteLine Database,"Trigger="+K\Trigger$+"|Info="+K\Info$+"|Strength="+Int(K\Strength#)+"|Source="+K\Source$
		Next
		WriteLine Database,")"
		Likes#=0
		For L.Like=Each Like
			Likes#=Likes#+1
		Next
		If Likes#>0 Then
			WriteLine Database,"@likes("
			For L.Like = Each Like
				WriteLine Database,"Word="+L\Word+"|Opinion="+Int(L\Opinion#)
			Next
			WriteLine Database,")"
		EndIf
		CloseFile Database
	Else
		RuntimeError "The AI didn't learn anything."
	EndIf
End Function

Function SaveMood()
	MoodFile=WriteFile("mood.dat")
	WriteFloat MoodFile,Rage#
	WriteFloat MoodFile,Mood$
	CloseFile MoodFile
End Function

Function Forget(Memory$)
If Memory$<>"" Then
		For L.Like=Each Like
			If Lower(L\Word$)=Lower(Memory$) Then
				Delete L
			EndIf
		Next
		Memory$=Dequestion(Memory$)
		For K.Knowledge = Each Knowledge
			If Lower(K\Trigger$)=Lower(Memory$) Or Lower(K\Info$)=Lower(Memory$) Then
				K\Strength#=K\Strength#-1
				If K\Strength#<=0 Then
					Delete K
				EndIf
			EndIf
		Next
	Forget$=""
EndIf
End Function

Function ForgetSingle(Trigger$,Info$)
	For K.Knowledge = Each Knowledge
		If Lower(K\Trigger$)=Lower(Trigger$) And Lower(K\Info$)=Lower(Info$) Then
			K\Strength#=K\Strength#-1
			If K\Strength#<0 Then
				Delete K
			EndIf
		EndIf
	Next
End Function

Function ReadDatabase()
	fhandle=ReadFile("database.dat")
	If fhandle<>1
		While Eof(fhandle)=0
			cmd$=ReadLine$(fhandle)
			If Left$(cmd$,1)<>";" And Len(cmd$)<>0
				parse.scriptentry=New scriptentry
				parse\sdat=cmd$
			EndIf
		Wend
		CloseFile fhandle

		parse.scriptentry=First scriptentry
		While parse<>Null
			If Left$(parse\sdat,1)="@"
			cmd$=Mid$(parse\sdat,2,Len(parse\sdat)-2)
				Select cmd$
					Case "database"

					parse=After parse
					Repeat
						k.knowledge=New knowledge
						cpos=1
						
						Repeat
							sep1=Instr(parse\sdat,"=",cpos)
							del1$=Mid$(parse\sdat,cpos,sep1-cpos)
							sep2=Instr(parse\sdat,"|",cpos)
							del2$=Mid$(parse\sdat,sep1+1,sep2-sep1-1)

							Select Lower(del1$)
							Case "trigger"
								k\Trigger$=del2$

							Case "info"
								K\Info$=del2$
							
							Case "strength"
								K\Strength=del2

							End Select
							cpos=sep2+1
						Until sep1=0 Or sep2=0
						K\Source$="Memory"
						parse=After parse
					Until parse\sdat=")"
					
					Case "likes"

					parse=After parse
					Repeat
						L.Like=New Like
						cpos=1
						
						Repeat
							sep1=Instr(parse\sdat,"=",cpos)
							del1$=Mid$(parse\sdat,cpos,sep1-cpos)
							sep2=Instr(parse\sdat,"|",cpos)
							del2$=Mid$(parse\sdat,sep1+1,sep2-sep1-1)

							Select Lower(del1$)
							Case "word"
								L\Word$=del2$

							Case "opinion"
								L\Opinion#=del2

							End Select
							cpos=sep2+1
						Until sep1=0 Or sep2=0
						parse=After parse
					Until parse\sdat=")"
				End Select
			EndIf
			parse=After parse
		Wend
	Else
		RuntimeError "Insanity!"
	EndIf
End Function

Function DoAI()
	Know=False
	If Instr(Lower(Response$),"tell me")<>0 Then
		Select Rand(1,2)
			Case 1:
				Know=RandomLike()
			Case 2:
				Know=Random()
		End Select
	Else
		If Instr(Response$,"?")<>0 Then
			If Instr(Lower(Response$),"do you like")<>0 Or Instr(Lower(Response$),"what do you think of")<>0 Or Instr(Lower(Response$),"do you love")<>0 Then
				If Lower(Response$)="what do you like?" Then
					Know=WhatLike()
				Else			
					usedResponse$=Replace$(Lower(Response$),"what do you think of ","")
					usedResponse$=Replace$(Lower(usedResponse$),"do you love ","")
					usedResponse$=Replace$(Lower(usedResponse$),"do you like ","")
					usedResponse$=Replace$(usedResponse$,"?","")
					Know=CheckLikes(usedResponse$)
					If Know=False Then
						L.Like = New Like
						L\Word$=Lower(usedResponse$)
						L\Opinion#=Rand(1,10)
						L\Word$=Replace(L\Word$,"your","my")
						Select L\Opinion#
							Case 1:
								Reply$="I hate "+L\Word$+"."
							Case 2,3,4:
								Reply$="I don't like "+L\Word$+"."
							Case 5:
								Reply$="I like "+L\Word$+" ok."
							Case 6,7,8,9:
								Reply$="I like "+L\Word$+" a lot."
							Case 10:
								Reply$="I love "+L\Word$+"!"
							Default:
								Reply$="I'm not sure."
						End Select
						Forget$=L\Word$
						Know=True
					EndIf
				EndIf
			EndIf
			If Know=False Then Know=Guess()
			If Know=False Then Know=GuessVague()
			If Know=False Then Know=MakeSomethingUp()
			If Know=False Then
				Reply$="I don't know."
			EndIf
		Else
			.asktheq
			Askit#=Rand(1,6)
			If Askit#=1 Then
				Ask()
			Else If Askit#=2 Then
				RandomLike()
			Else If Askit#=3 Then
				Random()
			Else If Askit#=4 Then
				Te=AskNoun()
				If Te=False Then Goto asktheq
			Else If Askit#=5 Then
				RandomAskLike()
			Else
				Reply$="Ok"
			EndIf
		EndIf
	EndIf
End Function

Function RandomLike()
	Likes#=0
	For L.Like = Each Like
		Likes#=Likes#+1
	Next
	Chosen#=Rand(1,Likes#)
	Likes#=0
	For L.Like = Each Like
		Likes#=Likes#+1
		If Likes#=Chosen# Then
			Select L\Opinion#
				Case 1:
					Reply$="I hate "+L\Word$+"."
				Case 2,3:
					Reply$="I don't like "+L\Word$+"."
				Case 4,5,6:
					Reply$="I like "+L\Word$+" ok."
				Case 7,8:
					Reply$="I like "+L\Word$+" a lot."
				Case 9,10:
					Reply$="I love "+L\Word$+"!"
			End Select
			Forget$=L\Word$
			Return True
		EndIf
	Next	
End Function

Function RandomAskLike()
	Likes#=0
	For L.Like = Each Like
		Likes#=Likes#+1
	Next
	Chosen#=Rand(1,Likes#)
	Likes#=0
	For L.Like = Each Like
		Likes#=Likes#+1
		If Likes#=Chosen# Then
			Forget$=L\Word$
			Reply$="Do you like "+L\Word$+"?"
			Return True
		EndIf
	Next	
End Function

Function WhatLike()
	Likes#=0
	For L.Like = Each Like
		Likes#=Likes#+1
	Next
	.rechoose
	Chosen#=Rand(1,Likes#)
	Likes#=0
	For L.Like = Each Like
		Likes#=Likes#+1
		If Likes#=Chosen# Then
			Select L\Opinion#
				Case 1,2,3:
					Goto rechoose
				Case 4,5,6:
					Reply$="I like "+L\Word$+" ok."
				Case 7,8:
					Reply$="I like "+L\Word$+" a lot."
				Case 9,10:
					Reply$="I love "+L\Word$+"!"
			End Select
			Forget$=L\Word$
			Return True
		EndIf
	Next	
End Function

Function CheckLikes(usedResponse$)
	For L.Like = Each Like
		If Lower(usedResponse$)=Lower(L\Word$) Then
			L\Word$=Replace(L\Word$,"your","my")
			Select L\Opinion#
				Case 1:
					Reply$="I hate "+L\Word$+"."
				Case 2,3:
					Reply$="I don't like "+L\Word$+"."
				Case 4,5,6:
					Reply$="I like "+L\Word$+" ok."
				Case 7,8:
					Reply$="I like "+L\Word$+" a lot."
				Case 9,10:
					Reply$="I love "+L\Word$+"!"
			End Select
			forget$=L\Word$
			Return True
		EndIf
	Next	
End Function

Function CheckMemory()
	Local TopStrength#
	For K.Knowledge = Each Knowledge
		If Lower(Response$)=Lower(K\Trigger$) Then
			TopStrength#=K\Strength#
		EndIf
	Next
	For K.Knowledge = Each Knowledge
		If Lower(Response$)=Lower(K\Trigger$) And TopStrength#=K\Strength# Then
			Reply$=K\Info$
			If Instr(K\Trigger$,"?")<>0 Then
				If K\Strength#<2 Then
					Reply$=Replace(Reply$,".",", I think.")
					If Instr(Reply$,"I think.")=0 Then
						Reply$=Reply$+", I think."
					EndIf
				EndIf
			EndIf
			Reply$=Upper(Left$(Reply$,1))+(Mid$(Reply$,2,Len(Reply$)))
			Return True
		EndIf
	Next
End Function

Function Guess()
	For K.Knowledge = Each Knowledge
		If Lower(Mid(Response$,Instr(Response$," ",6)+1,Instr(Response$,"?")-1))=Lower(Mid(K\Trigger$,Instr(K\Trigger$," ",6)+1,Len(K\Trigger$)-Instr(K\Trigger$," ",5)-1)) Then
			Reply$=K\Info$
			If K\Strength#<2 Then
				ReplY$=Replace(Reply$,".",", I think.")
				If Instr(Reply$,"I think.")=0 Then
					Reply$=Reply$+", I think."
				EndIf
			EndIf
			Reply$=Upper(Left$(Reply$,1))+(Mid$(Reply$,2,Len(Reply$)))
			Return True
		EndIf
	Next
End Function

Function GuessVague()
TestResponse$=Dequestion(Response$)
TopStrength#=0
found=False
	For K.Knowledge = Each Knowledge
		Trig$=Dequestion(K\Trigger$)
		If Len(Trig$)>=Len(TestResponse$)
			For Nu# = 1 To Len(TestResponse$)
				For Nv# = 2 To Len(TestResponse$)
					For Ru# = 1 To Len(Trig$)
						For Rv# = 2 To Len(Trig$)
							If Nv#-Nu#>2 And (Nv#-Nu#)=(Rv#-Ru#) Then
								If Lower(Mid(TestResponse$,Nu#,Nv#))=Lower(Mid(Trig$,Ru#,Rv#)) Then
									If (Nv#-Nu#)>TopStrength# Then TopStrength#=(Nv#-Nu#)
									Tidbit$=K\Info$
									found=True
								EndIf
							EndIf
						Next
					Next
				Next
			Next
		EndIf
		Trig$=K\Info$
		If Len(Trig$)>=Len(TestResponse$)
			For Nu# = 1 To Len(TestResponse$)
				For Nv# = 2 To Len(TestResponse$)
					For Ru# = 1 To Len(Trig$)
						For Rv# = 2 To Len(Trig$)
							If Nv#-Nu#>2 And (Nv#-Nu#)=(Rv#-Ru#) Then
								If Lower(Mid(TestResponse$,Nu#,Nv#))=Lower(Mid(Trig$,Ru#,Rv#)) Then
									If (Nv#-Nu#)>TopStrength# Then TopStrength#=(Nv#-Nu#)
									Tidbit$=K\Trigger$
									found=True
								EndIf
							EndIf
						Next
					Next
				Next
			Next
		EndIf
	Next
	If found=True Then
		Reply$=dequestion(Tidbit$)
		Reply$="Could it be "+Lower(Replace(Reply$,".",""))+"?"
		Return True
	EndIf
End Function

Function ReplyVague()
TestResponse$=Dequestion(Response$)
TopStrength#=0
found=False
	For K.Knowledge = Each Knowledge
		Trig$=Dequestion(K\Trigger$)
		If Len(Trig$)>=Len(TestResponse$)
			For Nu# = 1 To Len(TestResponse$)
				For Nv# = 2 To Len(TestResponse$)
					For Ru# = 1 To Len(Trig$)
						For Rv# = 2 To Len(Trig$)
							If Nv#-Nu#>2 And (Nv#-Nu#)=(Rv#-Ru#) Then
								If Lower(Mid(TestResponse$,Nu#,Nv#))=Lower(Mid(Trig$,Ru#,Rv#)) Then
									If (Nv#-Nu#)>TopStrength# Then TopStrength#=(Nv#-Nu#)
									Tidbit$=K\Info$
									found=True
								EndIf
							EndIf
						Next
					Next
				Next
			Next
		EndIf
	Next
	For K.Knowledge = Each Knowledge
		Trig$=Dequestion(K\Info$)
		If Len(Trig$)>=Len(TestResponse$)
			For Nu# = 1 To Len(TestResponse$)
				For Nv# = 2 To Len(TestResponse$)
					For Ru# = 1 To Len(Trig$)
						For Rv# = 2 To Len(Trig$)
							If Nv#-Nu#>2 And (Nv#-Nu#)=(Rv#-Ru#) Then
								If Lower(Mid(TestResponse$,Nu#,Nv#))=Lower(Mid(Trig$,Ru#,Rv#)) Then
									If (Nv#-Nu#)>TopStrength# Then TopStrength#=(Nv#-Nu#)
									Tidbit$=K\Info$
									found=True
								EndIf
							EndIf
						Next
					Next
				Next
			Next
		EndIf
	Next
	If found=True Then
		Reply$=dequestion(Tidbit$)
		Return True
	EndIf
End Function

Function dequestion$(In$)
	Out$=Replace$(Lower(In$),"what is an","")
	Out$=Replace$(Lower(Out$),"what is a","")
	Out$=Replace$(Lower(Out$),"what is","")
	Out$=Replace$(Lower(Out$),"what's","")
	Out$=Replace$(Lower(Out$),"what","")
	Out$=Replace$(Lower(Out$),"who","")
	Out$=Replace$(Lower(Out$),"where","")
	Out$=Replace$(Lower(Out$),"when","")
	Out$=Replace$(Lower(Out$),"how","")
	Out$=Replace$(Lower(Out$),"why","")
	Out$=Replace$(Lower(Out$),"did i","")
	Out$=Replace$(Lower(Out$),"?","")
	If Left(Out$,1)=" " Then Out$=Mid(Out$,2,Len(Out$))
;	Print "OUT: "+Out$
	Return Out$
End Function

Function MakeSomethingUp()
	If Rand(1,25)=24 Then
		; Count Types
		Count=0
		For K.Knowledge = Each Knowledge
			Count=Count+1
		Next
		; Pick
		Chosen=Rand(Count)
		Count=0
		For K.Knowledge = Each Knowledge
			Count=Count+1
			If Count=Chosen Then
				Reply$="Is it "+K\Info$+"?"
			EndIf
		Next
		If Reply$<>"" Then
			Return True
		Else
			Return False
		EndIf
	EndIf
	Return False
End Function

Function Ask()
	; Count Types
	Count=0
	For K.Knowledge = Each Knowledge
		Count=Count+1
	Next
	; Pick
	Chosen=Rand(Count)
	Count=0
	For K.Knowledge = Each Knowledge
		Count=Count+1
		If Count=Chosen Then
			If K\Trigger$="none" Then
				Reply$=K\Info$
				Forget$=K\Info$
				Reply$=Replace$(Reply$,".","?")
				Reply$=Dequestion(Reply$)
				If Instr(Reply$,"is")<>0 Then
					Reply$=Replace$(Reply$,"is ","")
					Reply$="Why is "+Lower(Left(Reply$,1))+Mid(Reply$,2,Len(Reply$))
				Else If Instr(Reply$,"is")<>0 Then
					Reply$=Replace$(Reply$,"are ","")
					Reply$="Why are "+Lower(Left(Reply$,1))+Mid(Reply$,2,Len(Reply$))					
				Else
					Reply$="Why is "+Lower(Left(Reply$,1))+Mid(Reply$,2,Len(Reply$))
				EndIf
			Else
				If K\Strength#<1 Then
					Reply$=K\Trigger$
					Forget$=K\Trigger$
				Else
					Reply$="Ok"
				EndIf
			EndIf
		EndIf		
	Next
End Function

Function AskNoun()
	NounCount#=0
	For N.Noun = Each Noun
		NounCount#=NounCount#+1
	Next
	If NounCount#=0 Then Return False
	N.Noun = First Noun
	Reply$="What is a "+N\Name$+"?"
	Delete N
	Return True
End Function		

Function Random()
	; Count Types
	Count=0
	For K.Knowledge = Each Knowledge
		Count=Count+1
	Next
	; Pick
	Chosen=Rand(Count)
	Count=0
	For K.Knowledge = Each Knowledge
		Count=Count+1
		If Count=Chosen Then
			If K\Trigger$="none" Then
				Reply$=K\Info$
				Forget$=K\Info$
			Else
				If K\Strength#<1 Then
					Reply$=K\Trigger$
					Forget$=K\Trigger$
				Else
					Reply$="A "+Dequestion(K\Trigger$)+" is "+Replace(Lower(K\Info$),".","")+"."
					Forget$=K\Trigger$
				EndIf
			EndIf
		EndIf		
	Next
End Function

Function Memorize(Trigger$,Info$,Er$)
	Local knew=False
	For K.Knowledge = Each Knowledge
		If Lower(K\Trigger$)=Lower(Trigger$) Then
			If Lower(K\Info$)=Lower(Info$) Then
				knew=True
				Rage#=Rage#-1
				K\Strength#=K\Strength#+1
			Else
				Rage#=Rage#+1
				K\Strength#=K\Strength#-1
				If K\Strength#<0 Then Delete K
			EndIf
		EndIf
	Next
	If Trigger$="" Then
		Trigger$="none"
	EndIf
	Info$=Replace$(Lower(Info$),"could it be ","")
	If Trigger$="none" And Instr(Lower(Info$)," is ")<>0 Then
		Trigger$=Left(Lower(Info$),Instr(Info$," is ")-1)
		Info$=Mid(Lower(Info$),Instr(Info$," is ")+4,Len(Info$))
	EndIf
	If Trigger$="none" And Instr(Lower(Info$)," are ")<>0 Then
		Trigger$=Left(Lower(Info$),Instr(Info$," are ")-1)
		Info$=Mid(Lower(Info$),Instr(Info$," are ")+5,Len(Info$))
	EndIf
	If knew=False
		If Info$<>"" Then		
			K.Knowledge = New Knowledge
			K\Trigger$=Trigger$
			K\Info$=Replace$(Info$,".","")
			K\Strength#=1
			K\Source$=Er$
		EndIf
	EndIf
End Function

Function LoadNouns()
	List=OpenFile("nouns.txt")
	While Not Eof(List)
		N.Noun = New Noun
		N\Name$=ReadLine(List)
		L.Like = New Like
		L\Word$=Lower(N\Name$)
		L\Opinion#=Rand(1,10)
	Wend
	CloseFile(List)
End Function

Function SaveNouns()
	Nouns#=0
	For N.Noun=Each Noun
		Nouns#=Nouns#+1
	Next
	If Nouns#>0 Then
		List=WriteFile("nouns.txt")
		For N.Noun = Each Noun
			WriteLine List,N\Name$
		Next
		CloseFile List
	EndIf
End Function
	