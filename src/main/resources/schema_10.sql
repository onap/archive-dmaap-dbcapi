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
	add column 	partition_count	varchar(10),
	add column	replication_count varchar(10)
;

-- set existing topics to have old default values, for display purposes
update topic
set partition_count = '2', replication_count = '1';


update dmaapbc_sch_ver set version = 10 where version = 9;
