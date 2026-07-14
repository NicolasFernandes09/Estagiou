USE db_estagiou;

ALTER TABLE usuarios
    MODIFY foto VARCHAR(255) NOT NULL DEFAULT '';

SET @indice_nome = (
    SELECT INDEX_NAME
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'usuarios'
      AND COLUMN_NAME = 'nome'
      AND NON_UNIQUE = 0
      AND INDEX_NAME <> 'PRIMARY'
    LIMIT 1
);

SET @remover_indice_nome = IF(
    @indice_nome IS NULL,
    'SELECT 1',
    CONCAT('ALTER TABLE usuarios DROP INDEX `', REPLACE(@indice_nome, '`', '``'), '`')
);

PREPARE comando FROM @remover_indice_nome;
EXECUTE comando;
DEALLOCATE PREPARE comando;
