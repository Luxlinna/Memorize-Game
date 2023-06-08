package com.example.memorizegame

import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import com.example.memorizegame.R.drawable.*
import android.widget.ImageButton
import android.widget.Toast


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var buttons: List<ImageButton>
    private lateinit var cards: List<MemoryCard>
    private var indexOfSingleSelectedCard: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val images = mutableListOf(ic_bitcoin, ic_bunny, ic_plane, ic_lightning)
        // Add each image twice so we can create pair.
        images.addAll(images)
        // Randomize the order of images
        images.shuffle()

        buttons = listOf(imageButton1, imageButton2, imageButton3, imageButton4, imageButton5,
            imageButton6, imageButton7,imageButton8)

        cards = buttons.indices.map { index ->
            MemoryCard(images[index], false, false)
        }

        buttons.forEachIndexed{ index, button ->
            button.setOnClickListener{
                Log.i(TAG,"button clicked !!")
                // Update the Model
                updateModels(index)
                // Update the UI for the game
                updateViews()
            }
        }
    }

    private fun updateViews() {
        cards.forEachIndexed { index, card ->
            val button = buttons[index]
            if (card.isMatched) {
                button.alpha = 0.5f
            }
            button.setImageResource(if (card.isFaceUp) card.identifier else ic_code)
        }
        // the trhee line below is equal with this one line
        // button.setImageResource(if (card.isFaceUp) images[index] else ic_code)
        /*
        if (card.isFaceUp) {
            button.setImageResource(images[index])
        } else {
            button.setImageResource(ic_code)
        }
         */
    }

    private fun updateModels(position: Int) {
        val card = cards[position]
        // Error checking
        if ( card.isFaceUp) {
            Toast.makeText(this, "Invalite move!", Toast.LENGTH_SHORT).show()
            return
        }
        // Three cases
        // 0 cards previously flipped over =>  restor card + flip over the selected card
        // 1 carad previously flipped over => flip over the selected card + check if the image matched
        // 2 card previously flipped over => restor card + flip over the selected card
        if (indexOfSingleSelectedCard == null) {
            // 0 or 2 selected cards previously
            restoreCards()
            indexOfSingleSelectedCard = position
        } else {
            // exactly 1 cards  was selected  previously
            checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp
    }

    private fun restoreCards() {
        for (card in cards) {
            if(!card.isMatched) {
                card.isFaceUp = false
            }
        }
    }

    private fun checkForMatch(position1: Int, position2: Int) {
        if (cards[position1].identifier == cards[position2].identifier) {
            Toast.makeText(this, "Match found!!", Toast.LENGTH_SHORT).show()
            cards[position1].isMatched = true
            cards[position2].isMatched = true
        }
    }
}