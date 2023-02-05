package b2infosoft.milkapp.com.Interface;


import b2infosoft.milkapp.com.Model.BeanProductItem;

public interface ProductOnClickListner {
    void onProductAdapterClick(BeanProductItem beanProductItem);

    void onCartUpdate(int position);
}
