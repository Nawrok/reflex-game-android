package pl.embedded.reflex.enums;

public enum Language
{
    ENGLISH("en"),
    POLISH("pl");

    private final String lang_id;

    Language(final String lang_id)
    {
        this.lang_id = lang_id;
    }

    @Override
    public String toString()
    {
        return lang_id;
    }
}
