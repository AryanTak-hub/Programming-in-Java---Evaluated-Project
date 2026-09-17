# Problem Statement

## Problem Statement

Small and mid-sized jewellery stores in India typically track stock,
pricing and billing manually — in notebooks or basic spreadsheets. This is
error-prone for two reasons specific to the jewellery trade: (1) the price
of an item is not fixed, it depends on the *current* gold/silver rate at
the time of sale plus a making charge, so prices must be recalculated
constantly rather than just looked up; and (2) GST (3% for jewellery in
India) must be applied correctly on every invoice. Manual tracking makes it
easy to under- or over-charge, lose track of stock, and have no record of
which items actually sell.

**JewelCraft** is a command-line Java application that solves this by
centralising inventory, pricing, billing and reporting in one place, with
prices computed automatically from live rate + making charge, and every
sale validated against real stock before it's confirmed.

## Scope of the Project

In scope:
- Managing a catalogue of jewellery items (add/update/delete/search) with
  automatic unit pricing from weight, metal rate and making charge.
- Managing customers.
- Raising multi-item invoices with discount and 3% GST calculated
  automatically, with stock validated and deducted atomically per bill.
- Sales and stock reports (revenue, GST collected, best-sellers, low-stock
  alerts).
- Persistent storage across runs via CSV files (no external DB needed).

Out of scope (possible future work, not attempted here):
- Multi-user login / role-based access control.
- A graphical or web UI (this is deliberately a CLI tool, per the
  assignment's "must be executable via command line" requirement).
- Payment gateway integration.
- Cloud/network storage — data is local to the machine running it.

## Target Users

- A jewellery store owner or staff member (e.g. someone running the shop
  floor) who needs to look up stock, quote a price, or raise a bill quickly.
- A store manager reviewing sales performance and stock levels.

## High-Level Features

1. **Inventory Management** — add, update, delete and search jewellery
   items; automatic price computation; low-stock alerts.
2. **Billing** — build an invoice for a customer across multiple items,
   with stock validation, discount and 3% GST applied automatically.
3. **Reports & Analytics** — sales summary, top-selling items, low-stock
   report, all derived live from stored data.
4. **Customer Management** — maintain a simple customer directory that
   bills are raised against.
