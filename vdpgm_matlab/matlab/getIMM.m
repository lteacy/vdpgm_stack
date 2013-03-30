function [mu,Sigma,weights] = getIMM(data)


%*******************************************************************************
%  Set up the algorithm options to use accelerated variational Dirichlet
%  process Gaussian mixture algorithm. NOTE this is the only algorithm
%  implemented by vdpgm which does not use a fixed number of components.
%  
%  We may wish to change this, so that any of the 4 implemented algorithms can
%  be selected.
%*******************************************************************************
opts = mkopts_avdp;
opts.suppress_output=true; % don't print rubbish to screen

%*******************************************************************************
%  Perform inference to approximate the posterior
%*******************************************************************************
results = vdpgm(data,opts);
prior = results.hp_prior;
posterior = results.hp_posterior;

%*******************************************************************************
% Calculate the component weights for our approximate mixture.
% Eventually, we should change this to match the equation given in the
% NIPs 2006 paper - but this should will be close enough, especially for large
% N=size(data,2).
%*******************************************************************************
weights = posterior.Nc;
weights(end) = weights(end)+prior.alpha;
weights = weights ./ sum(weights);

%*******************************************************************************
% Get the component means
%*******************************************************************************
mu = posterior.m;

%*******************************************************************************
% Put the expected component variances into a single 3-D array
% s.t. sigma(:,:,k) is the covariance of the kth component.
%*******************************************************************************
[nDims, nComponents] = size(mu);
Sigma = zeros(nDims,nDims,nComponents);
for k=1:nComponents
   Sigma(:,:,k) = posterior.B{k} / posterior.eta(k); 
end



