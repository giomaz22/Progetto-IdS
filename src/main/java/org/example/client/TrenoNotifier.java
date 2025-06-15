package org.example.client;

import io.grpc.stub.StreamObserver;
import org.example.grpc.IscrivimiTrenoSpeRequest;
import org.example.grpc.IscrivimiTrenoSpeResponse;
import org.example.grpc.TrenicalServiceGrpc;

public class TrenoNotifier {
    private TrenicalServiceGrpc.TrenicalServiceStub asyncStub;

    public TrenoNotifier(TrenicalServiceGrpc.TrenicalServiceStub asyncStub) {
        this.asyncStub = asyncStub;
    }
    // andamento treno specifico (chiesto da chiunque) OK
    public void andamentoTreno(String idTreno, String cfUt) {
        IscrivimiTrenoSpeRequest request = IscrivimiTrenoSpeRequest.newBuilder()
                .setIdTreno(idTreno)
                .build();

        asyncStub.andamentoTrenoSpecifico(request, new StreamObserver<IscrivimiTrenoSpeResponse>() {
            @Override
            public void onNext(IscrivimiTrenoSpeResponse value) {
                System.out.println("[NOTIFICA] " + value.getMessaggio());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Errore " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Treno OK");
            }
        });
    }
}
