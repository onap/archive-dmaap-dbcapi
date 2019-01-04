---
-- ============LICENSE_START=======================================================
-- OpenECOMP - org.onap.dbcapi
-- ================================================================================
-- Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
-- ================================================================================
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- ============LICENSE_END=========================================================
---


@alter table topic
	add column 	publisher_role	varchar(100),
	add column	subscriber_role varchar(100)
;
@alter table mr_client
	add column 	client_identity	varchar(100)
;

update dmaapbc_sch_ver set version = 11 where version = 10;
