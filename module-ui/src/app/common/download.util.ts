import { HttpResponse } from '@angular/common/http';

export function downloadFileWithName(blob: Blob, fileName: string): void {
  const objectUrl = URL.createObjectURL(blob);
  const link = Object.assign(document.createElement('a'), {
    style: 'display: none',
    target: 'blank',
    href: objectUrl,
    download: decodeURIComponent(fileName),
  });

  document.body.appendChild(link);
  link.click();
  URL.revokeObjectURL(objectUrl);
  link.remove();
}

export function downloadFile(fileResponse: HttpResponse<Blob>): string {
  if (fileResponse.body == null) {
    return '';
  }

  const fileName =
    fileResponse?.headers.get('content-disposition')?.match(/filename\*=UTF-8''([^;]+)/)?.[1] ?? 'no-name-file';

  downloadFileWithName(fileResponse.body, fileName);
  return fileName;
}
