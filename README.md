# Cosc322AmazonAITeam3
1. Introduction

This is the last report for the COSC322 Project, in which we had to use the resources we
had covered in class to create an AI that could play the Queen of the Amazons game. In the
class competition, our team placed fifth. Both the bot's technical specifications and process
observations are covered in this study.

Using important AI techniques including state-space search, heuristic evaluation, and
pruning algorithms, the project's goal was to create and deploy a competitive AI agent for
The Game of the Amazons. Building a bot that could make judgments in real time in a highly
dynamic, high-branching game environment and testing its competitiveness in a class
tournament scenario were the ultimate objectives. The biggest constraint we faced was time.

2. System Overview / Project Architecture

The design was centered around a modular architecture, with components dedicated to state
representation, move generation, heuristic evaluation, and search algorithms. During initial
development, significant emphasis was placed on building an efficient move generator and
optimizing pruning techniques to handle the large branching factor of the game tree. The use
of game-phase-specific heuristics was integral to achieving adaptive and strategic
decision-making.

The AmazonAI class houses the implementation that communicates with the server. It
translates all moves from the format of the server to the format of a double array before
processing. It checks the game state and the heuristic type defined and sends different
instructions to the search classes depending on what heuristic the AI was defined to use and
the game state . A player object is initialized with a name, password and heuristic type. It
stores the game board as a double array, queen locations, a boolean value isBlack, the GUI,
the client and the heuristic type. Upon getting a start message or move message from the
server, it will send the move to the IterativeDeepening class with the specified heuristic and
a specified evaluation time defined within the call function to IterativeDeepening.
The IterativeDeepening class is called from an AmazonAI object with the board information
and the previous move made along with which colour the player is. It runs a search in a
specified time constraint that returns the best move based on the heuristic given.
The Heuristics class house all of the heuristics used and Breadth First Search helper
methods in order to calculate them

MoveGenerator is a class which houses the move logic. It's used to generate child branches
in the IterativeDeepening search tree and it's used as a helper for calculating our Mobility
heuristic.

The Heuristics class defines all the heuristics: KingTerritoryControl, Mobility, and Minimum
Distance. The heuristic or heuristic combinations that are used in the evaluation of
IterativeDeepening depends on what heuristic the AI is defined to use
QueenLocationBoardPair and valueMovePair are custom objects that are used to store the
queen locations of any given board and the evaluation of any given new move respectively.
valueMovePair is used in the alpha beta iterative deepening algorithm as defined in the
textbook (citation needed). queenLocationBoardPair is used to save queen locations for
simulated boards in the search tree so that the algorithm does not need to search the entire
board for the queens whenever a board is evaluated.

3. Implementation Details

The early development phase involved interpreting the provided API and ensuring that the
game engine was functional and testable. This required verifying board states, handling
message communication from the server, and building a preliminary representation of the
board including queen positions and blocked tiles.
The algorithm used in IterativeDeepening is an alpha beta iterative deepening search that
has an evaluation time as opposed to an evaluation depth, so that we can use the full 30
seconds given to us as per the rules of the tournament. Late into the tournament we had a
problem surrounding our timer: sometimes the parallel processing streams that we originally
had would keep running after the timer was up due to the current recursion being on a
separate thread to the timer. We used a ForkJoinTask to resolve the issue and made sure
the evaluation stopped at the correct time, and we have not had a problem with that since.
Alpha Beta was used because we didn't use a memory table for each evaluation level, and
Alpha Beta pruning cuts out searching a lot of the leaves, making it more efficient.
Heuristics were conceptualized according to game phase: opening, midgame, and endgame.
Scenario-specific strategies were also outlined for handling cases like blocked queens or
potential board splits.

We created 3 heuristics in total. One was Mobility, which measured the amount of moves
that were available to all queens on any particular move, and returned the difference
between our possible moves and the opponents possible moves. This is useful in the
middlegame when the queens begin to get blocked because it can identify moves which can
see if and when the movement of the enemy pieces becomes restricted and find moves that
could possibly box them in. Our second heuristic is King Distance, which measures how
many moves a player needs to reach a square if all of their pieces move like chess Kings,
and gives a score 1 or -1 to each square depending on if white or black can get there first.
This is useful in the opening and middlegame for finding and/or establishing regions where
our Amazons have exclusive or majority influence over a certain area and wall it off from the
enemy (1). Our third heuristic is Minimum Distance. This is useful in the late middlegame,
endgame and terminal phases. This gives each square an evaluation 1 or -1 based on if a
player can reach that square with a queen faster (or at all) than the opponent can. This
allows our bot to see moves that could hinder enemy queens from entering key open areas
quickly or block them entirely, giving exclusive control to our Amazons. Occupying the last
open areas gives a high score and so does blocking the enemy from entering them. In the
terminal phase, this heuristic is especially useful for filling in tiles efficiently (2) In the opening
and middlegame, a weighted combination of 1 * Mobility + 1 * King Distance is used. In the
endgame, Minimum Distance is used. Our bot starts using MinimumDistance if the total
amount of reachable squares to us + the total number of reachable squares for the opponent
falls below 80 or the the total amount of reachable squares to our queens in one move + the
total number of reachable squares for the opponents queens in one move falls below 40.
There are a few other combinations that were coded for AI vs AI testing. This includes:
Mobility and Minimum Distance, Minimum Distance only, and Mobility only.

4. Testing and Evaluation

Testing during early phases included verifying move legality, board consistency after state
transitions, and ensuring that the bot made valid responses to server prompts.
A series of AI-vs-AI simulations were planned to evaluate performance, adjust heuristic
weights, and refine move ordering strategies. Profiling tools were identified for future use in
locating performance bottlenecks, especially in high-depth searches and move evaluation.
We used a VPN to simulate the game environment, regularly tested new bot versions
against older ones, and had a team member play against the bot to assess its
decision-making.

5. Discussion

Several technical and conceptual challenges were identified during development.
Our greatest hurdle for the start of this project was implementing the GUI. Due to an
oversight on our part we did not understand how to properly update the game state for the
GUI and this implementation was delayed for a while. However, this did lead us to create a
matrix array description and move list description that later turned out to be very useful for
debugging purposes.

In order to stay within real-time limitations, the game's high computing complexity
necessitated severe pruning and effective move sorting algorithms. It was also difficult to
create phase-appropriate and balanced heuristics. The complexities of the game rules also
required careful attention to ensure the integrity of game states and debug board transitions.

6. Conclusion and Future Work

Our bot uses Alpha Beta pruning with Iterative Deepening with a ForkJoinTask to find the
best move in a set amount of time. It's quite strong in the endgame and terminal phase, but
we got caught out in the middlegame before implementing the King distance heuristic. We
communicated and coordinated decently well with our ideas. In the future, improvements can
be made to branching and testing discipline as there was a time when broken code was
mistakenly pushed to the main branch and there were a lack of Junit tests from some of our
team members, making it difficult to judge code quality and functionality of certain branches.
These problems were eventually resolved but cost more time.

Future development could be approached in a number of ways. We had initially considered
integrating machine learning methods, specifically training a neural network for board state
evaluation or move suggestion. This would improve heuristic accuracy and adaptability,
greater pruning efficiency and better strategic depth. Additionally, implementing advanced
time management logic, such as dynamic adjustment of depth or evaluation scope based on
game phase, could further optimize performance in time-constrained environments in
comparison to the more rudimentary time limiting logic that is currently implemented.
The bot could also be improved over time (“Learn”) by implementing support for self-play
data collection and automated heuristic tuning. However, due to time constraints and
implementation complexity, using a deep learning model to predict optimal moves or board
evaluations was not pursued. This approach, while promising, was considered outside the
project scope due to extensive training time requirements. As such we settled with
optimising our current implementation even through the competition. That said, this work can
be considered to be the next steps taken for anyone wanting to develop a more complex yet
more functional bot.

8. References

https://www.solitairelaboratory.com/amazons.html
https://core.ac.uk/download/pdf/81108035.pdf
https://library2.msri.org/books/Book42/files/muller.pdf

