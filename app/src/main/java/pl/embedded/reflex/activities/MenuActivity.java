package pl.embedded.reflex.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import pl.embedded.reflex.R;

public class MenuActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        loadAuthors();
    }

    public void changeLanguage(View view)
    {
        switch (view.getId())
        {
            case (R.id.english_lang_button):
            {
                updateLocale(Locale.ENGLISH);
                break;
            }
            case (R.id.polish_lang_button):
            {
                updateLocale(new Locale("pl", "PL"));
            }
        }
    }

    public void startGame(View view)
    {
        startActivity(new Intent(this, GameActivity.class));
    }

    private void loadAuthors()
    {
        StringBuilder sb = new StringBuilder();
        for (String author : getResources().getStringArray(R.array.authors_list))
        {
            sb.append(author).append(System.lineSeparator());
        }
        TextView authors = findViewById(R.id.authors_list);
        authors.setText(sb.toString().trim());
    }
}