SELECT
	sample.ncbi_sample_id AS "NCBI Accession",
	sample.isolate,
	sample.isolation_source AS "Isolation Source",
	sample.computed_strain AS "Strain",
	sample.computed_serovar AS "Serovar",
	sample.collection_year,
	organism.organism_name,
	owner.institution AS "Submitting Institution",
	sample.sra AS "SRA",
	sample.location,
	project.project_title
FROM 
	sample
	LEFT OUTER JOIN sample_owner ON (sample.sample_id = sample_owner.sample_id)
	LEFT OUTER JOIN owner ON (sample_owner.owner_id = owner.owner_id)
	LEFT OUTER JOIN organism ON (sample.organism_id = organism.organism_id)
	LEFT OUTER JOIN sample_project ON (sample.sample_id = sample_project.sample_id) 
	LEFT OUTER JOIN project ON (sample_project.project_id = project.project_id)