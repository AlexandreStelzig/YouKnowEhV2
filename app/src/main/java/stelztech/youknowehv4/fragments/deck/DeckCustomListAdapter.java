package stelztech.youknowehv4.fragments.deck;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.activities.MainActivityManager;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.deck.Deck;

/**
 * Created by alex on 1/20/2018.
 */

public class DeckCustomListAdapter extends BaseAdapter {


    private Context context;
    private DeckListFragment deckListFragment;

    private LayoutInflater inflater = null;

    public DeckCustomListAdapter(DeckListFragment deckListFragment, Context context) {
        // TODO Auto-generated constructor stub
        super();
        this.deckListFragment = deckListFragment;
        this.context = context;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return deckListFragment.getDeckList().size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView deckName;
        TextView numberOfCardsLabel;
        LinearLayout deckLayout;
        LinearLayout deckOptionLayout;
        LinearLayout reorderLayout;
        TextView reorderTextView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder = null;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.custom_deck_item, null);
            holder = new Holder();

            holder.deckName = (TextView) convertView.findViewById(R.id.custom_deck_item_name);
            holder.numberOfCardsLabel = (TextView) convertView.findViewById(R.id.custom_deck_item_nb_cards_label);
            holder.deckLayout = (LinearLayout) convertView.findViewById(R.id.custom_deck_item_layout);
            holder.deckOptionLayout = (LinearLayout) convertView.findViewById(R.id.custom_deck_option_layout);
            holder.reorderLayout = (LinearLayout) convertView.findViewById(R.id.custom_deck_reorder_layout);
            holder.reorderTextView = (TextView) convertView.findViewById(R.id.custom_deck_item_reorder_textview);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }


        // all


        holder.deckOptionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deckListFragment.setIndexSelected(position);
                deckListFragment.getActivity().openContextMenu(deckListFragment.getListView());
            }
        });


        holder.deckName.setText(deckListFragment.getDeckList().get(position).getDeckName());

//            int nbCards = dbManager.getCardsFromDeck(deckList.get(position).getDeckId()).size();
        int nbCards = deckListFragment.getNbCardsInDeckList().get(position);

        if (nbCards == 1)
            holder.numberOfCardsLabel.setText("1 Card");
        else
            holder.numberOfCardsLabel.setText(nbCards + " Cards");


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!deckListFragment.isDeckOrdering()) {
                    deckListFragment.setIndexSelected(position);
                    ((MainActivityManager) deckListFragment.getActivity()).displayDeckInfo(deckListFragment.getDeckList().get(position).getDeckId());
                }
            }
        });


        if (deckListFragment.isDeckOrdering()) {
            holder.reorderLayout.setVisibility(View.VISIBLE);
            holder.deckOptionLayout.setVisibility(View.GONE);
            int positionInDeck = deckListFragment.getDeckList().get(position).getPosition();
            String positionString = positionInDeck + "";
            if (positionString.length() == 1)
                positionString = "0" + positionString;
            holder.reorderTextView.setText(positionString);
            convertView.setOnLongClickListener(null);
        } else {
            holder.reorderLayout.setVisibility(View.GONE);
            holder.deckOptionLayout.setVisibility(View.VISIBLE);
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    deckListFragment.setIndexSelected(position);
                    return false;
                }
            });
        }


        holder.reorderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeckOrderingDialog(deckListFragment.getDeckList().get(position));
            }
        });


        holder.deckLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_normal));


        return convertView;

    }

    private void showDeckOrderingDialog(final Deck deck) {


        View viewPicker = View.inflate(context, R.layout.custom_number_picker_dialog, null);
        android.app.AlertDialog.Builder db = new android.app.AlertDialog.Builder(context);
        db.setView(viewPicker);
        db.setTitle("Deck Order: " + deck.getDeckName());

        final NumberPicker np = (NumberPicker) viewPicker.findViewById(R.id.numberPicker1);
        np.setMaxValue(deckListFragment.getDeckList().size());
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });

        np.setValue(deck.getPosition());


        db.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int newPosition = np.getValue();
                deckListFragment.changeDeckPosition(newPosition, deck);
            }
        });
        android.app.AlertDialog dialog = db.show();

    }


}
