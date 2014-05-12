package zemberek.solr;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ZemberekTurkishStemmingFilterFactory extends TokenFilterFactory {

    private static final Logger logger = LoggerFactory.getLogger(ZemberekTurkishStemmingFilterFactory.class);

    public ZemberekTurkishStemmingFilterFactory(final Map<String, String> args) {
        super(args);
        logger.info("[zemberek-turkish-stemming-filter-factory-created-successfully]");
    }

    @Override
    public TokenStream create(final TokenStream input) {
        return new ZemberekTurkishStemmingFilter(input);
    }
}
