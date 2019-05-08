package xyz.lazysoft.a3amp.components.preview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import xyz.lazysoft.a3amp.R

class AmpPreviewAdapter(private val units: List<AmpUnit>) :
        RecyclerView.Adapter<AmpPreviewAdapter.PreviewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.amp_preview_card, parent, false)
        return PreviewHolder(v)
    }

    override fun getItemCount(): Int {
        return units.size
    }

    override fun onBindViewHolder(holder: PreviewHolder, position: Int) {
        val ampUnit = units[position]
        holder.title.text =  ampUnit.title.capitalize()
        holder.switch.visibility = if (ampUnit.sw) View.VISIBLE else View.INVISIBLE
        holder.card.setOnClickListener {
            if (!ampUnit.sw || holder.switch.isChecked)
                ampUnit.onClick.invoke()
        }
    }

    class PreviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.preview_text)
        val switch: Switch = itemView.findViewById(R.id.amp_preview_card_switch)
        val card: CardView = itemView.findViewById(R.id.preview_card)
    }
}