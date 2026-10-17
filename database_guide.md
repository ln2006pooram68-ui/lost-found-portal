
# Lost and Found Portal — Database Guide

We use Java and SQLite.

## Users table
- id: unique user ID
- name: user's name
- email: unique email address
- password_hash: securely hashed password
- role: USER or ADMIN

## Items table
- id: unique item ID
- name: item name
- description: item details
- location: lost/found location
- item_date: date of incident
- type: LOST or FOUND
- status: PENDING, APPROVED, or REJECTED
- reporter_id: ID of the reporting user

## Claims table
- id: unique claim ID
- item_id: ID of the item being claimed
- claimant_id: ID of the claiming user
- status: PENDING, APPROVED, or REJECTED

## Shared rules
- Use DBConnection.getConnection() to connect to SQLite.
- Do not create separate databases for individual features.
- Do not store passwords as plain text.
- Only authorized admins can approve or reject reports and claims.
- Keep the table and field names consistent.
