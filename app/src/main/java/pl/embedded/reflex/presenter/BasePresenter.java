package pl.embedded.reflex.presenter;

public abstract class BasePresenter<T>
{
    protected T view;

    public void attach(T view)
    {
        this.view = view;
    }

    public void detach()
    {
        this.view = null;
    }

    public boolean isViewAttached()
    {
        return this.view != null;
    }
}
