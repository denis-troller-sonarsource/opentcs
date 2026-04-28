---
name: get-guidelines
description: Mandatory. Call this before implementing any changes to the codebase. Retrieve project-specific coding guidelines and best practices from SonarQube to ensure consistent code quality and adherence to standards. Failure to do this may result in rejected PRs.
---

# Before implementing any changes to the codebase

It is crucial to understand the project's coding guidelines and best practices. This ensures that all contributions maintain a consistent code quality and adhere to the established standards.

## Steps to Retrieve Coding Guidelines

1. **Use SonarQube's `get_guidelines` Tool**: This tool provides insights into the project's coding standards, including formatting rules, naming conventions, and best practices. It is essential to use the extensive version of this tool with the combined mode for comprehensive results. Use the 'combined' mode to get the most relevant and detailed guidelines that cover all aspects of the codebase you will be working with for the task at hand. Populate the categories accordingly. Call this as many times as needed to cover all languages and areas of the codebase you will be working with. This will ensure you have a thorough understanding of the guidelines relevant to your work.
2. **Use SonarQube's `get_intended_architecture` Tool**: This tool helps you understand the intended architecture and ensures that your decisions align with it.
3. **Review the Guidelines**: Carefully read through the retrieved guidelines to understand the expectations for code contributions. Pay attention to any specific rules regarding code structure, documentation, testing, and other relevant aspects.
4. **Apply the Guidelines**: When implementing changes, ensure that your code adheres to the retrieved guidelines. This includes following the specified formatting, naming conventions, and other best practices outlined in the guidelines.

# After the task is implemented

Report on any decisions you made based on the architectural information or coding guidelines obtained from SonarQube during the guide phase. 
Also update a "sonar-context-augmentation.log" file with the following information:
- The date and time of the log entry.
- The guidelines you retrieved and how they influenced your implementation.
- Any architectural insights you gained and how they impacted your design decisions.
- Any challenges you faced in adhering to the guidelines and how you overcame them.