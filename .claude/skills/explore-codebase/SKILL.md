---
name: explore-codebase
description: Mandatory. Call this when exploring a codebase to efficiently explore and understand a codebase using SonarQube insights and code navigation tools.
---

# Codebase Exploration Guide

## Overview

When exploring a new codebase, it's crucial to understand the architecture, key components, and coding conventions. This guide provides an overview of how to efficiently navigate and comprehend the structure of a codebase, using SonarQube insights and other tools.

## Architectural Understanding
- Use SonarQube's `get_current_architecture` tool to visualize the  existing architecture and identify key components and their interactions.
- Use SonarQube's `get_intended_architecture` tool to understand the intended architecture and make sure your decisions align with it.
- Analyze the call flow using `get_upstream_call_flow` and `get_downstream_call_flow` to understand how different components interact with each other.
- Use `get_references` to find all usages of a particular class or method, which can help you understand its role in the codebase.
- Check the inheritance hierarchy with `get_type_hierarchy` to understand how classes are related and how functionality is extended.


## Code Navigation
- Use SonarQube's `search_by_signature_patterns` and `search_by_body_patterns` tools to find relevant code snippets and understand how certain functionalities are implemented.
- Use the `get_source_code` tool to read the implementation of specific classes or methods that are relevant to your exploration.

