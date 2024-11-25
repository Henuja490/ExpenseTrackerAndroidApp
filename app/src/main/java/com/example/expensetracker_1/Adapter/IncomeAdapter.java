package com.example.expensetracker_1.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker_1.Domain.Income;
import com.example.expensetracker_1.R;

import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {

    private final List<Income> incomeList;
    private final IncomeClickListener clickListener;
    private final Context context;

    public interface IncomeClickListener {
        void onIncomeClick(Income income);
    }

    public IncomeAdapter(List<Income> incomeList, IncomeClickListener clickListener, Context context) {
        this.incomeList = incomeList;
        this.clickListener = clickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        Income income = incomeList.get(position);
        holder.titleTextView.setText(income.getTitle());
        holder.amountTextView.setText(income.getAmount());
        holder.date.setText(income.getDate());

        // Handle click events
        holder.itemView.setOnClickListener(v -> clickListener.onIncomeClick(income));
    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView amountTextView;
        TextView date;
        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.item_title);
            amountTextView = itemView.findViewById(R.id.item_subtitle);
            date = itemView.findViewById(R.id.item_date);
        }
    }
}
