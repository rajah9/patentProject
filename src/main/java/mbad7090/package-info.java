/**
 * Created by Rajah on 11/13/2015.
 */
package mbad7090;

/*

Two main packages, model and xml. Model is used to model patent information, with an abstract
Patent which is the parent to PatentAbstract (yes, confusing) and PatentGrant.

XML has teh following structure:

model
  XMLMapReduce (used to parse patent XML files)
      PatentGrantXmlMapReduce (used to generate CSV files for patent grant XML files)
      PatentAbstractXmlMapReduce (used to generate CSV files for patent abstract XML files)
          CountXmlMapReduce (abstract; used to count things)
              PatentClassXml (used to count what patent classes we have)
              PatentCountMapReduce (used to count types of patents, ours vs theirs)
 */