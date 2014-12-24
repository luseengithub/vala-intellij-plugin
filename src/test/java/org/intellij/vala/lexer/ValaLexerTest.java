package org.intellij.vala.lexer;

import com.intellij.psi.TokenType;
import org.intellij.vala.psi.ValaTypes;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ValaLexerTest {

    private ValaLexer valaLexer;

    @Before
    public void before() {
        valaLexer = new ValaLexer();
    }

    @Test
    public void shouldDetectString() throws IOException {
        String content = "\"blah\"";
        valaLexer.start(content);

        assertThat(valaLexer.getTokenType(), is(ValaTypes.STRING));
    }

    @Test
    public void shouldDetectInteger() throws IOException {
        valaLexer.start("2");

        assertThat(valaLexer.getTokenType(), is(ValaTypes.NUMBER));
    }

    @Test
    public void shouldHandleEmptyContent() throws IOException {
        valaLexer.start("");

        assertThat(valaLexer.getTokenType(), is(nullValue()));
    }

    @Test
    public void shouldHandleWhiteSpace() throws IOException {
        valaLexer.start(" \t\t\t  ");

        assertThat(valaLexer.getTokenType(), is(TokenType.WHITE_SPACE));
    }

    @Test
    public void shouldAdvanceOnMultipleContents() throws IOException {
        valaLexer.start("\"string\" 3");
        valaLexer.advance();
        valaLexer.advance();

        assertThat(valaLexer.getTokenType(), is(ValaTypes.NUMBER));
    }

    @Test
    public void shouldHandleInvalidCharacters() throws IOException {
        valaLexer.start("$%");

        assertThat(valaLexer.getTokenType(), is(TokenType.BAD_CHARACTER));
    }

    @Test
    public void shouldProceedAfterSeeingBadCharacter() throws IOException {
        valaLexer.start("%^ 3");
        valaLexer.advance();
        valaLexer.advance();
        valaLexer.advance();

        assertThat(valaLexer.getTokenType(), is(ValaTypes.NUMBER));
    }

    @Test
    public void shouldRecognizeClassKeyword() throws IOException {
        valaLexer.start("class");

        assertThat(valaLexer.getTokenType(), is(ValaTypes.KEY_CLASS));
    }

    @Test
    public void shouldRecognizeIdentifiers() throws IOException {
        valaLexer.start("some_identifier");

        assertThat(valaLexer.getTokenType(), is(ValaTypes.IDENTIFIER));
        assertThat(valaLexer.getTokenText(), is(equalTo("some_identifier")));
    }

}
