package zemberek.solr;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zemberek.morphology.apps.TurkishMorphParser;
import zemberek.morphology.parser.MorphParse;

import java.io.IOException;
import java.util.List;

/**
 *
 * This class converts a token into its Turkish stem if it finds any stem via Zemberek. The stem that is used is the one that is returned first in the MorphParse list.
 *
 */
public final class ZemberekTurkishStemmingFilter extends TokenFilter {

    private static final Logger logger = LoggerFactory.getLogger(ZemberekTurkishStemmingFilter.class);

    private static final TurkishMorphParser turkishMorphParser;

    static {
        try {
            turkishMorphParser = TurkishMorphParser.createWithDefaults();
            logger.info("[zemberek-initialized-successfully]");
        } catch (IOException e) {
            throw new RuntimeException("Creating ZemberekTurkishStemmingFilter failed.", e);
        }
    }

    private final CharTermAttribute termAttribute;

    private final KeywordAttribute keywordAttribute;

    public ZemberekTurkishStemmingFilter(final TokenStream input) {
        super(input);
        this.termAttribute = addAttribute(CharTermAttribute.class);
        this.keywordAttribute = addAttribute(KeywordAttribute.class);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        final boolean incremented = input.incrementToken();

        if (incremented && !keywordAttribute.isKeyword()) {
            updateTermWithItsStemIfTurkish(termAttribute.toString());
        }

        return incremented;
    }

    private String updateTermWithItsStemIfTurkish(final String token) {
        final String stem = findStem(token);

        if (stem != null) {
            termAttribute.copyBuffer(stem.toCharArray(), 0, stem.length());
            termAttribute.setLength(stem.length());
        }

        return stem;
    }

    private String findStem(final String token) {
        final List<MorphParse> parses = turkishMorphParser.parse(token);

        return parses != null && parses.size() > 0 && parses.get(0).getStems() != null && parses.get(0).getStems().size() > 0 ? parses.get(0).getStems().get(0) : null;
    }
}
