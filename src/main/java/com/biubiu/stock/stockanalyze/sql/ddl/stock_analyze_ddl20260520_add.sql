ALTER TABLE stock_watchlist_bk_stock DROP INDEX uk_bk_stock;

ALTER TABLE stock_watchlist_bk_stock
    ADD UNIQUE INDEX `uk_bk_stock_reason`(`bk_id`, `stock_code`, `add_reason`);