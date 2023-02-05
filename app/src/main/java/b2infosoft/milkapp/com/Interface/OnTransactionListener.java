package b2infosoft.milkapp.com.Interface;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.BeanUserTransaction;

/**
 * Created by Microsoft on 23-Aug-19.
 */

public interface OnTransactionListener {

    public void setTransactionList(ArrayList<BeanUserTransaction> transactionList,  double totalCredit,
                                   double totalDebit,String fileUrl);
}
