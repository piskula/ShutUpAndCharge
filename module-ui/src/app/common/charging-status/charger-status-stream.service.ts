import { Inject, Injectable, Optional } from '@angular/core';
import { Observable } from 'rxjs';
import { BASE_PATH, ChargerStatusDTO } from '@suac/api';

@Injectable({
  providedIn: 'root',
})
export class ChargerStatusStreamService {

  constructor(@Optional() @Inject(BASE_PATH) private readonly basePath: string | string[]) {
  }

  public connect(): Observable<ChargerStatusDTO> {
    return new Observable<ChargerStatusDTO>(subscriber => {
      const basePath = Array.isArray(this.basePath) ? this.basePath[0] : (this.basePath ?? '.');
      const eventSource = new EventSource(`${basePath}/info/chargerStatus/stream`);

      eventSource.addEventListener('status', (event: MessageEvent) => {
        subscriber.next(JSON.parse(event.data) as ChargerStatusDTO);
      });

      eventSource.onerror = () => {
        // EventSource retries automatically; nothing to surface beyond letting the last known status stand.
      };

      return () => eventSource.close();
    });
  }

}
