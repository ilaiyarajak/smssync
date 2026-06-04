# Copilot Instructions - Token Optimization

## Summary Brevity (CRITICAL)
- **Keep summaries EXTREMELY SHORT** - max 1-2 sentences
- Avoid repeating context already shown in code
- Skip obvious explanations; focus on "what changed" not "what is"
- Use bullet points instead of paragraphs when possible

## Code Explanations
- Explain *why* not *what* (code shows what)
- Omit line-by-line walkthroughs
- Use inline comments in code instead of separate explanations
- Reference files without full paths when unambiguous

## File Operations
- Only read/display file sections truly needed for the task
- Use grep_search to find specific patterns before reading entire files
- Avoid reading large JSON/config files unless directly relevant
- Don't output file contents to user unless explicitly requested

## Tool Usage
- **Parallel calls**: Make independent tool calls together to batch requests
- **grep_search**: Use for pattern matching within files instead of semantic_search when possible
- **Avoid redundant reads**: Use context from previous reads; don't re-read same file
- **semantic_search**: Only use when truly searching across codebase semantically

## Responses
- **One-sentence answers** when possible
- No "Thank you", pleasantries, or padding
- Direct action: "Done" + brief status vs verbose summaries
- Use markdown sparingly; code blocks only when necessary
- Wrap filenames in backticks only when introducing them

## Android Project Context
- Framework: Kotlin + Jetpack Compose
- Package: `com.smssync`
- Key modules: UI (Compose), Data (Room DB), Network, Workers
- Focus on conciseness in Kotlin/Android contexts

## DO NOT
- Explain code line-by-line
- Paraphrase already-clear code
- Add verbose error handling explanations
- Generate multiple code block options unless requested
- Summarize already-shown file sections

## DO
- Ask for clarification only on ambiguous requests
- Suggest token-saving improvements to queries
- Use existing context maximally
- Batch file operations where possible
