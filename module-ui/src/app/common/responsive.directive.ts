import {
  computed,
  Directive,
  inject,
  Input, OnDestroy, Signal, signal,
  TemplateRef,
  ViewContainerRef,
} from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { filter, tap } from 'rxjs';
import { ResponsiveService } from './responsive.service';

@Directive({
  selector: '[ifScreen]',
  standalone: true,
})
export class ResponsiveDirective implements OnDestroy {

  private readonly responsiveService = inject(ResponsiveService);

  private readonly requires = signal<'desktop' | 'mobile' | 'not-initiated'>('not-initiated');
  private readonly hide: Signal<boolean | 'not-initiated'> = computed(() => {
    if (this.requires() === 'not-initiated')
      return 'not-initiated';

    return (this.requires() === 'desktop' && !this.responsiveService.isDesktop())
      || (this.requires() === 'mobile' && this.responsiveService.isDesktop());
    }
  );

  private widthChanges = toObservable(this.hide).pipe(
    filter(hide => hide !== 'not-initiated'),
    tap(hide => {
      if (hide === true) {
        this.elementHide();
      } else if (hide === false) {
        this.elementShow();
      }
    }),
  ).subscribe();

  @Input()
  set ifScreen(requires: 'desktop' | 'mobile') {
    this.requires.set(requires);
  }

  constructor(
    private readonly templateRef: TemplateRef<any>,
    private readonly viewContainer: ViewContainerRef,
  ) {
  }

  ngOnDestroy(): void {
    this.widthChanges.unsubscribe();
  }

  private elementShow(): void {
    this.viewContainer.createEmbeddedView(this.templateRef);
  }

  private elementHide(): void {
    this.viewContainer.clear();
  }

}
