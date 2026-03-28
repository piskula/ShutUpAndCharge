import { Configuration, ConfigurationParameters } from '@suac/api';

/**
 * Services generated vie openapi generator are always processing response as Blob due to unlucky generated code logic.
 * This Config changes this behavior by not allowing to change default JSON via calling selectHeaderAccept.
 */
export class BlobFixConfiguration extends Configuration {
  constructor(params: ConfigurationParameters = {}) {
    super(params);
  }

  /**
   * Override selectHeaderAccept to always return undefined.
   * This changes behavior in all services using this Configuration instance.
   */
  override selectHeaderAccept(_accepts: string[]): string | undefined {
    return undefined;
  }
}
