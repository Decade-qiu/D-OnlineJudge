import { marked } from 'marked';
import katex from 'katex';

// Configure marked options if needed
marked.setOptions({
    breaks: true, // Enable line breaks
    gfm: true,    // Enable GitHub Flavored Markdown
});

export const renderMarkdown = (text: string | undefined): string => {
    if (!text) return '';

    // 1. Protect Math delimiters
    // We replace $$...$$ and $...$ with placeholders to prevent marked from messing them up.
    // Using a random string or specific pattern as placeholder.
    const mathBlocks: string[] = [];
    const protectedText = text.replace(/(\$\$.*?\$\$|\$.*?\$)/gs, (match) => {
        mathBlocks.push(match);
        return `%%%MATH_BLOCK_${mathBlocks.length - 1}%%%`;
    });

    // 2. Render Markdown
    let html = marked.parse(protectedText) as string;

    // 3. Restore Math and Render KaTeX
    html = html.replace(/%%%MATH_BLOCK_(\d+)%%%/g, (_match, index) => {
        const math = mathBlocks[parseInt(index, 10)];
        if (!math) return '';

        if (math.startsWith('$$')) {
            // Display math
            try {
                return katex.renderToString(math.slice(2, -2), {
                    displayMode: true,
                    throwOnError: false
                });
            } catch (e) {
                return math;
            }
        } else {
            // Inline math
            try {
                return katex.renderToString(math.slice(1, -1), {
                    displayMode: false,
                    throwOnError: false
                });
            } catch (e) {
                return math;
            }
        }
    });

    return html;
};
