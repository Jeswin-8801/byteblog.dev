import { CacheKey, Deserializer, ObjectMapper } from 'json-object-mapper';

export class Role {
  id?: number;
  privilege?: string;
}

@CacheKey('RoleDeserializer')
export class RoleDeserializer implements Deserializer {
  deserialize = (array: [JSON]): Role[] => {
    let roleArray = [];
    for (const value of array)
      roleArray.push(ObjectMapper.deserialize(Role, value));
    return roleArray;
  };
}
