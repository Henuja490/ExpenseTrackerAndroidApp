package com.example.expensetracker_1.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker_1.Domain.Expense;
import com.example.expensetracker_1.R;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final List<Expense> expenseList;
    private final ExpenseClickListener clickListener;
    private final Context context;

    public interface ExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    public ExpenseAdapter(List<Expense> expenseList, ExpenseClickListener clickListener, Context context) {
        this.expenseList = expenseList;
        this.clickListener = clickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.titleTextView.setText(expense.getTitle());
        holder.amountTextView.setText(expense.getAmount());
        holder.Date.setText(expense.getDate());

        // Handle click events
        holder.itemView.setOnClickListener(v -> clickListener.onExpenseClick(expense));
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView amountTextView;
        TextView Date;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.item_title);
            amountTextView = itemView.findViewById(R.id.item_subtitle);
            Date = itemView.findViewById(R.id.item_date);
        }
    }
}
