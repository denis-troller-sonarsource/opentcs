---
name: verify-changes
description: Mandatory. Call this when verifying the changes you implemented, to check that they adhere to the project's coding guidelines and best practices using SonarQube's analysis tools. Failure to do this may result in rejected PRs.
---

# Verify Changes Guide

## Overview
After implementing changes to the codebase, it is essential to verify that the modifications adhere to the project's coding guidelines and best practices. This guide outlines the steps to use SonarQube's analysis tools to ensure that your changes maintain code quality and consistency.
## Steps to Verify Changes
1. **Run Advanced Code Analysis**: For every new or modified file, call the `run_advanced_code_analysis` tool to perform a comprehensive analysis of the changes. This will help identify any issues related to code quality, security vulnerabilities, or adherence to coding standards.
2. **Review Issues and Apply Fixes**: For every issue flagged by the analysis, use the `show_rule` tool to understand the specific rule that was violated. Implement fixes based on the rationale and recommended guidance provided by the rule. It is crucial to address all **CRITICAL** and **HIGH** issues before committing the code, as these can significantly impact the quality and security of the codebase.
3. **Re-run Analysis**: After applying fixes, re-run the advanced code analysis to ensure that all issues have been resolved and that no new issues have been introduced. This iterative process helps maintain a high standard of code quality and ensures that your changes are in line with the project's guidelines and best practices.
4. **Report on Changes**: After successfully verifying your changes, report on any decisions you made based on the guidelines or architectural information obtained from SonarQube during the guide phase. Additionally, report any issues that were flagged by the SonarQube analysis and the steps you took to correct them. This transparency helps maintain a clear record of the changes made and the rationale behind them, which can be valuable for future reference and for other team members.