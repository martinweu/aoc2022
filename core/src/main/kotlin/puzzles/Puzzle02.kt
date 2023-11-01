package puzzles

import Puzzle

class Puzzle02P1 : Puzzle() {
    override fun solve(lines: List<String>): String {
        var score = 0
        for(line in lines)
        {
            var roundScore = 0
            val hands = line.split(" ")
            val opponent = hands[0]
            val myHand = hands[1]

            if((opponent == "A" && myHand == "X") || (opponent == "B" && myHand == "Y") || (opponent == "C" && myHand == "Z"))
            {
                roundScore += 3
            }
            else if((opponent == "C" && myHand == "X") || (opponent == "A" && myHand == "Y") || (opponent == "B" && myHand == "Z"))
            {
                roundScore += 6
            }
            roundScore += myHand[0]-'X' + 1
            score += roundScore
        }
        return score.toString()
    }
}

class Puzzle02P2 : Puzzle() {
    override fun solve(lines: List<String>): String {
        var score = 0
        for(line in lines)
        {
            var roundScore = 0
            val hands = line.split(" ")
            val opponent = hands[0]

            val endOfGame = hands[1]

            val myHand = if(endOfGame == "X")
            {
                if(opponent == "A")
                {
                    "Z"
                }
                else if(opponent == "B")
                {
                    "X"
                }
                else
                {
                    "Y"
                }
            }
            else if (endOfGame == "Y")
            {
                if(opponent == "A")
                {
                    "X"
                }
                else if(opponent == "B")
                {
                    "Y"
                }
                else
                {
                    "Z"
                }
            }
            else
            {
                if(opponent == "A")
                {
                    "Y"
                }
                else if(opponent == "B")
                {
                    "Z"
                }
                else
                {
                    "X"
                }
            }


            if((opponent == "A" && myHand == "X") || (opponent == "B" && myHand == "Y") || (opponent == "C" && myHand == "Z"))
            {
                roundScore += 3
            }
            else if((opponent == "C" && myHand == "X") || (opponent == "A" && myHand == "Y") || (opponent == "B" && myHand == "Z"))
            {
                roundScore += 6
            }
            roundScore += myHand[0]-'X' + 1
            score += roundScore
        }
        return score.toString()
    }

}