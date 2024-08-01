

DELETE FROM ndr_code_set where code = '4j'
    OR code_description = 'd4T-3TC-NVP';

INSERT INTO ndr_code_set (code_set_nm, code, code_description, alt_description, sys_description)
         VALUES ('ARV_REGIMEN', '4f', 'D4T-3TC-NVP', '', 'Lamivudine+Nevirapine+Stavudine_3');